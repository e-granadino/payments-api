package com.aplicacorp.payments_api.payment.repository;

import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldSaveAndFindPaymentById() {
        // Arrange
        PaymentEntity payment = new PaymentEntity(
                "CUS-001",
                new BigDecimal("150.75"),
                "USD",
                "PENDING",
                Instant.parse("2026-09-19T10:00:00Z")
        );

        // Act
        PaymentEntity savedPayment = paymentRepository.save(payment);

        Optional<PaymentEntity> result =
                paymentRepository.findById(savedPayment.getId());

        // Assert
        assertThat(result).isPresent();

        PaymentEntity persistedPayment = result.orElseThrow();

        assertThat(persistedPayment.getId())
                .isEqualTo(savedPayment.getId());

        assertThat(persistedPayment.getCustomerId())
                .isEqualTo("CUS-001");

        assertThat(persistedPayment.getAmount())
                .isEqualByComparingTo("150.75");

        assertThat(persistedPayment.getCurrency())
                .isEqualTo("USD");

        assertThat(persistedPayment.getStatus())
                .isEqualTo("PENDING");

        assertThat(persistedPayment.getCreatedAt())
                .isEqualTo(Instant.parse("2026-09-19T10:00:00Z"));
    }

    @Test
    void shouldFindPaymentsByCustomerId() {
        // Arrange
        paymentRepository.saveAll(List.of(
                payment(
                        "CUS-001",
                        "100.00",
                        "USD",
                        "PENDING",
                        "2026-09-18T10:00:00Z"
                ),
                payment(
                        "CUS-001",
                        "200.00",
                        "USD",
                        "PENDING",
                        "2026-09-19T10:00:00Z"
                ),
                payment(
                        "CUS-002",
                        "300.00",
                        "USD",
                        "PENDING",
                        "2026-09-19T11:00:00Z"
                )
        ));

        // Act
        List<PaymentEntity> result =
                paymentRepository.findByCustomerId("CUS-001");

        // Assert
        assertThat(result)
                .hasSize(2)
                .allMatch(payment ->
                        payment.getCustomerId().equals("CUS-001"));
    }

    @Test
    void shouldFindPaymentsWithinDateRange() {
        // Arrange
        paymentRepository.saveAll(List.of(
                payment(
                        "CUS-001",
                        "100.00",
                        "USD",
                        "PENDING",
                        "2026-09-18T10:00:00Z"
                ),
                payment(
                        "CUS-002",
                        "200.00",
                        "USD",
                        "PENDING",
                        "2026-09-19T10:00:00Z"
                ),
                payment(
                        "CUS-003",
                        "300.00",
                        "USD",
                        "PENDING",
                        "2026-09-20T10:00:00Z"
                )
        ));

        Instant from =
                Instant.parse("2026-09-18T00:00:00Z");

        Instant to =
                Instant.parse("2026-09-19T23:59:59Z");

        // Act
        List<PaymentEntity> result =
                paymentRepository.findByCreatedAtBetween(from, to);

        // Assert
        assertThat(result)
                .hasSize(2);
    }

    @Test
    void shouldFindPaymentsByCustomerAndDateRange() {
        // Arrange
        paymentRepository.saveAll(List.of(
                payment(
                        "CUS-001",
                        "100.00",
                        "USD",
                        "PENDING",
                        "2026-09-18T10:00:00Z"
                ),
                payment(
                        "CUS-001",
                        "200.00",
                        "USD",
                        "PENDING",
                        "2026-09-19T10:00:00Z"
                ),
                payment(
                        "CUS-002",
                        "300.00",
                        "USD",
                        "PENDING",
                        "2026-09-19T10:00:00Z"
                )
        ));

        Instant from =
                Instant.parse("2026-09-19T00:00:00Z");

        Instant to =
                Instant.parse("2026-09-19T23:59:59Z");

        // Act
        List<PaymentEntity> result =
                paymentRepository.findByCustomerIdAndCreatedAtBetween(
                        "CUS-001",
                        from,
                        to
                );

        // Assert
        assertThat(result)
                .hasSize(1);

        assertThat(result.getFirst().getCustomerId())
                .isEqualTo("CUS-001");

        assertThat(result.getFirst().getAmount())
                .isEqualByComparingTo("200.00");
    }

    @Test
    void shouldReturnEmptyListWhenCustomerDoesNotExist() {
        // Act
        List<PaymentEntity> result =
                paymentRepository.findByCustomerId("UNKNOWN");

        // Assert
        assertThat(result).isEmpty();
    }

    private PaymentEntity payment(
            String customerId,
            String amount,
            String currency,
            String status,
            String timestamp
    ) {
        return new PaymentEntity(
                customerId,
                new BigDecimal(amount),
                currency,
                status,
                Instant.parse(timestamp)
        );
    }
}
