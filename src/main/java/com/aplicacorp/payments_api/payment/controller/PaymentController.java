package com.aplicacorp.payments_api.payment.controller;

import com.aplicacorp.payments_api.common.dto.ApiErrorResponse;
import com.aplicacorp.payments_api.common.dto.PaymentApiResponse;
import com.aplicacorp.payments_api.payment.dto.CreatePaymentRequest;
import com.aplicacorp.payments_api.payment.dto.PaymentResponse;
import com.aplicacorp.payments_api.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(
        name = "Payments",
        description = "Payment processing and query operations"
)
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Create a payment",
            description = "Creates a payment, persists it and sends it to the core system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment request",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PostMapping
    public ResponseEntity<PaymentApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentApiResponse.success(response));
    }


    @Operation(
            summary = "Retrieve payments",
            description = """
        Retrieves payments using optional customer and date filters.
        When no filters are provided, all payments are returned.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public ResponseEntity<PaymentApiResponse<List<PaymentResponse>>> getPayments(
            @Parameter(
                    description = "Customer ID",
                    example = "CUS-001"
            )
            @RequestParam(required = false)
            String customerId,

            @Parameter(
                    description = "Start of payment timestamp range",
                    example = "2026-09-01T00:00:00Z"
            )
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,

            @Parameter(
                    description = "End of payment timestamp range",
                    example = "2026-09-30T23:59:59Z"
            )
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to
    ) {

        List<PaymentResponse> payments =
                paymentService.findPayments(customerId, from, to);

        return ResponseEntity.ok(
                PaymentApiResponse.success(payments)
        );
    }
}
