package com.aplicacorp.payments_api.payment.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePaymentRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidPayment() {
        CreatePaymentRequest request = validRequest();

        Set<?> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectMissingCustomerId() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                null,
                new BigDecimal("100.00"),
                "USD",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(v ->
                        v.getMessage().equals("CustomerId is required"));
    }

    @Test
    void shouldRejectBlankCustomerId() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "   ",
                new BigDecimal("100.00"),
                "USD",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(v ->
                        v.getMessage().equals("CustomerId is required"));
    }

    @Test
    void shouldRejectZeroAmount() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "CUS-001",
                BigDecimal.ZERO,
                "USD",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(v ->
                        v.getMessage().equals("Amount must be greater than zero"));
    }

    @Test
    void shouldRejectNegativeAmount() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "CUS-001",
                new BigDecimal("-10.00"),
                "USD",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(v ->
                        v.getMessage().equals("Amount must be greater than zero"));
    }

    @Test
    void shouldRejectInvalidCurrency() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "CUS-001",
                new BigDecimal("100.00"),
                "usd",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(v ->
                        v.getMessage().equals(
                                "Currency must be a 3-letter uppercase ISO code"
                        ));
    }

    @Test
    void shouldRejectMissingTimestamp() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                "CUS-001",
                new BigDecimal("100.00"),
                "USD",
                null
        );

        assertThat(validator.validate(request))
                .anyMatch(v ->
                        v.getMessage().equals("Timestamp is required"));
    }

    private CreatePaymentRequest validRequest() {
        return new CreatePaymentRequest(
                "CUS-001",
                new BigDecimal("100.00"),
                "USD",
                Instant.parse("2026-09-19T10:00:00Z")
        );
    }
}