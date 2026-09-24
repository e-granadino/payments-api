#!/usr/bin/env python3
"""
Payments API report generator.

Consumes:
    GET /api/v1/payments

and generates a CSV summary grouped by customer.

Configuration can be provided through environment variables or command-line
arguments. Command-line arguments take precedence.

Environment variables:
    API_URL        Default: http://localhost:8080
    API_USERNAME   Default: payment-user
    API_PASSWORD   Default: payment-password
    FROM_DATE      Default: 2026-09-01T00:00:00Z
    TO_DATE        Default: 2026-09-23T23:59:59Z
    OUTPUT_FILE    Default: payments-report.csv

Example:
    python payments_report.py

    python payments_report.py \
        --from-date 2026-09-01T00:00:00Z \
        --to-date 2026-09-23T23:59:59Z \
        --output payments.csv
"""

import argparse
import csv
import json
import os
import sys
from collections import defaultdict
from decimal import Decimal, InvalidOperation, ROUND_HALF_UP
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.request import Request, urlopen


DEFAULT_API_URL = "http://localhost:8080"
DEFAULT_USERNAME = "payment-user"
DEFAULT_PASSWORD = "payment-password"
DEFAULT_FROM_DATE = "2026-09-01T00:00:00Z"
DEFAULT_TO_DATE = "2026-09-23T23:59:59Z"
DEFAULT_OUTPUT_FILE = "payments-report.csv"

API_PATH = "/api/v1/payments"
REQUEST_TIMEOUT_SECONDS = 10
MONEY_PLACES = Decimal("0.01")


class PaymentsApiError(Exception):
    """Raised when the Payments API cannot be consumed successfully."""


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate a per-customer payment summary CSV."
    )

    parser.add_argument(
        "--api-url",
        default=os.getenv("API_URL", DEFAULT_API_URL),
        help="Payments API base URL.",
    )
    parser.add_argument(
        "--username",
        default=os.getenv("API_USERNAME", DEFAULT_USERNAME),
        help="Basic Authentication username.",
    )
    parser.add_argument(
        "--password",
        default=os.getenv("API_PASSWORD", DEFAULT_PASSWORD),
        help="Basic Authentication password.",
    )
    parser.add_argument(
        "--from-date",
        default=os.getenv("FROM_DATE", DEFAULT_FROM_DATE),
        help="Start of the payment date range in ISO-8601 format.",
    )
    parser.add_argument(
        "--to-date",
        default=os.getenv("TO_DATE", DEFAULT_TO_DATE),
        help="End of the payment date range in ISO-8601 format.",
    )
    parser.add_argument(
        "--output",
        default=os.getenv("OUTPUT_FILE", DEFAULT_OUTPUT_FILE),
        help="Output CSV file.",
    )

    return parser.parse_args()


def fetch_payments(
    api_url: str,
    username: str,
    password: str,
    from_date: str,
    to_date: str,
) -> list[dict]:
    base_url = api_url.rstrip("/")
    query = urlencode(
        {
            "from": from_date,
            "to": to_date,
        }
    )

    url = f"{base_url}{API_PATH}?{query}"

    request = Request(
        url,
        headers={
            "Accept": "application/json",
        },
        method="GET",
    )

    # urllib's HTTPPasswordMgr is unnecessary for one request. Basic Auth is
    # sent explicitly because the API uses stateless HTTP Basic Authentication.
    import base64

    credentials = f"{username}:{password}".encode("utf-8")
    encoded_credentials = base64.b64encode(credentials).decode("ascii")
    request.add_header("Authorization", f"Basic {encoded_credentials}")

    try:
        with urlopen(request, timeout=REQUEST_TIMEOUT_SECONDS) as response:
            raw_body = response.read().decode("utf-8")

    except HTTPError as exc:
        if exc.code == 401:
            raise PaymentsApiError(
                "Authentication failed. Check API_USERNAME and API_PASSWORD."
            ) from exc

        if exc.code == 403:
            raise PaymentsApiError(
                "Access denied by the Payments API."
            ) from exc

        try:
            response_body = exc.read().decode("utf-8", errors="replace")
        except Exception:
            response_body = ""

        message = (
            f"Payments API returned HTTP {exc.code}."
        )

        if response_body:
            message += f" Response: {response_body}"

        raise PaymentsApiError(message) from exc

    except URLError as exc:
        reason = getattr(exc, "reason", exc)
        raise PaymentsApiError(
            f"Unable to connect to Payments API at {base_url}. "
            f"Reason: {reason}"
        ) from exc

    except TimeoutError as exc:
        raise PaymentsApiError(
            f"Connection to Payments API timed out after "
            f"{REQUEST_TIMEOUT_SECONDS} seconds."
        ) from exc

    try:
        payload = json.loads(raw_body)
    except json.JSONDecodeError as exc:
        raise PaymentsApiError(
            "Payments API returned invalid JSON."
        ) from exc

    if payload.get("success") is not True:
        raise PaymentsApiError(
            "Payments API returned an unsuccessful response."
        )

    data = payload.get("data")

    if not isinstance(data, list):
        raise PaymentsApiError(
            "Payments API response does not contain a valid 'data' array."
        )

    return data


def build_customer_summary(payments: list[dict]) -> list[dict]:
    summaries = defaultdict(
        lambda: {
            "total_amount": Decimal("0"),
            "payment_count": 0,
        }
    )

    for payment in payments:
        customer_id = payment.get("customerId")

        if not customer_id:
            raise PaymentsApiError(
                "A payment is missing the required 'customerId' field."
            )

        amount = payment.get("amount")

        if amount is None:
            raise PaymentsApiError(
                f"Payment for customer '{customer_id}' is missing 'amount'."
            )

        try:
            decimal_amount = Decimal(str(amount))
        except (InvalidOperation, ValueError) as exc:
            raise PaymentsApiError(
                f"Invalid amount for customer '{customer_id}': {amount}"
            ) from exc

        summaries[customer_id]["total_amount"] += decimal_amount
        summaries[customer_id]["payment_count"] += 1

    result = []

    for customer_id, summary in summaries.items():
        total_amount = summary["total_amount"]
        payment_count = summary["payment_count"]

        average_amount = total_amount / payment_count

        result.append(
            {
                "customer_id": customer_id,
                "total_amount": total_amount.quantize(
                    MONEY_PLACES,
                    rounding=ROUND_HALF_UP,
                ),
                "payment_count": payment_count,
                "average_payment": average_amount.quantize(
                    MONEY_PLACES,
                    rounding=ROUND_HALF_UP,
                ),
            }
        )

    # Sort by total amount descending, then customer ID for deterministic output.
    result.sort(
        key=lambda item: (-item["total_amount"], item["customer_id"])
    )

    return result


def write_csv(summaries: list[dict], output_file: str) -> None:
    output_path = Path(output_file)

    try:
        with output_path.open(
            "w",
            newline="",
            encoding="utf-8",
        ) as csv_file:
            writer = csv.DictWriter(
                csv_file,
                fieldnames=[
                    "customer_id",
                    "total_amount",
                    "payment_count",
                    "average_payment",
                ],
            )

            writer.writeheader()
            writer.writerows(summaries)

    except OSError as exc:
        raise PaymentsApiError(
            f"Unable to write CSV file '{output_path}': {exc}"
        ) from exc


def main() -> int:
    args = parse_arguments()

    try:
        payments = fetch_payments(
            api_url=args.api_url,
            username=args.username,
            password=args.password,
            from_date=args.from_date,
            to_date=args.to_date,
        )

        summaries = build_customer_summary(payments)

        write_csv(
            summaries=summaries,
            output_file=args.output,
        )

    except PaymentsApiError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 1

    print(
        f"Report generated successfully: {args.output} "
        f"({len(summaries)} customers, {len(payments)} payments)"
    )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
