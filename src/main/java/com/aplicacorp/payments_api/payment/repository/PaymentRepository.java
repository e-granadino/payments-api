package com.aplicacorp.payments_api.payment.repository;

import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findByCustomerId(String customerId);

    List<PaymentEntity> findByCreatedAtBetween(
            Instant from,
            Instant to
    );

    List<PaymentEntity> findByCustomerIdAndCreatedAtBetween(
            String customerId,
            Instant from,
            Instant to
    );
}
