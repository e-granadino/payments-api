package com.aplicacorp.payments_api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard API error response")
public record ApiErrorResponse(
        @Schema(example = "2026-09-21T10:30:00Z")
        Instant timestamp,

        @Schema(example = "400")
        int status,

        @Schema(example = "Bad Request")
        String error,

        @Schema(example = "Validation failed")
        String message,

        @Schema(example = "/api/v1/payments")
        String path,
        Map<String, String> details
) {
}
