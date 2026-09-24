package com.aplicacorp.payments_api.payment.repository;

import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class PaymentRepositoryOracleIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldPersistAndRetrievePaymentFromOracle() {
        // Arrange
        PaymentEntity payment = new PaymentEntity(
                "CUS-ORACLE-001",
                new BigDecimal("123.45"),
                "USD",
                "PENDING",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        // Act
        PaymentEntity savedPayment = paymentRepository.save(payment);

        PaymentEntity retrievedPayment = paymentRepository
                .findById(savedPayment.getId())
                .orElseThrow();

        // Assert
        assertThat(savedPayment.getId()).isNotNull();

        assertThat(retrievedPayment.getId())
                .isEqualTo(savedPayment.getId());

        assertThat(retrievedPayment.getCustomerId())
                .isEqualTo("CUS-ORACLE-001");

        assertThat(retrievedPayment.getAmount())
                .isEqualByComparingTo("123.45");

        assertThat(retrievedPayment.getCurrency())
                .isEqualTo("USD");

        assertThat(retrievedPayment.getCreatedAt())
                .isEqualTo(Instant.parse("2026-09-19T10:00:00Z"));
    }
}
