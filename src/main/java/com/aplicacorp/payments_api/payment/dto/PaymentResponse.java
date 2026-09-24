package com.aplicacorp.payments_api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Payment response")
public record PaymentResponse(
        @Schema(
                description = "Unique payment identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Customer identifier",
                example = "CUS-001"
        )
        String customerId,

        @Schema(
                description = "Payment amount",
                example = "150.75"
        )
        BigDecimal amount,

        @Schema(
                description = "Three-letter ISO currency code",
                example = "USD"
        )
        String currency,

        @Schema(
                description = "Payment status",
                example = "PROCESSED"
        )
        String status,

        @Schema(
                description = "Payment createdAt",
                example = "2026-09-24T15:00:00Z"
        )
        Instant createdAt
) { }
