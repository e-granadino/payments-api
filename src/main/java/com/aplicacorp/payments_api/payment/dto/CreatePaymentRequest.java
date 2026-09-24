package com.aplicacorp.payments_api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Request used to create a payment")
public record CreatePaymentRequest(

        @Schema(
                description = "Customer identifier",
                example = "CUS-001",
                maxLength = 50
        )
        @NotBlank(message = "CustomerId is required")
        @Size(max = 50, message = "CustomerId must not exceed 50 characters")
        String customerId,

        @Schema(
                description = "Payment amount",
                example = "150.75"
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than zero"
        )
        BigDecimal amount,

        @Schema(
                description = "Three-letter ISO currency code",
                example = "USD"
        )
        @NotBlank(message = "Currency is required")
        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Currency must be a 3-letter uppercase ISO code"
        )
        String currency,

        @Schema(
                description = "Payment timestamp in ISO-8601 format",
                example = "2026-09-19T15:00:00Z"
        )
        Instant createdAt
) { }
