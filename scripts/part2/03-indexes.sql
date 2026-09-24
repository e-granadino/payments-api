/*
 * Part 2 - Index
 *
 * Supports the top-customers query by allowing Oracle
 * to narrow payments using the equality predicate on STATUS
 * and the range predicate on CREATED_AT before performing
 * the grouping and customer join.
 *
 * CUSTOMER_ID is included to support the subsequent join.
 */

CREATE INDEX IDX_PAYMENTS_STATUS_CREATED_CUSTOMER
    ON PAYMENTS (
        STATUS,
        CREATED_AT,
        CUSTOMER_ID
    );