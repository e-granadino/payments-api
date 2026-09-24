package com.aplicacorp.payments_api.payment.service;

import com.aplicacorp.payments_api.common.exception.InvalidPaymentFilterException;
import com.aplicacorp.payments_api.integration.core.CorePaymentClient;
import com.aplicacorp.payments_api.integration.core.PaymentXmlMapper;
import com.aplicacorp.payments_api.payment.dto.CreatePaymentRequest;
import com.aplicacorp.payments_api.payment.dto.PaymentResponse;
import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import com.aplicacorp.payments_api.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {

    private static final String INITIAL_STATUS = "PENDING";
    private final PaymentRepository paymentRepository;
    private final PaymentXmlMapper paymentXmlMapper;
    private final CorePaymentClient corePaymentClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentXmlMapper paymentXmlMapper,
            CorePaymentClient corePaymentClient
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentXmlMapper = paymentXmlMapper;
        this.corePaymentClient = corePaymentClient;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        PaymentEntity payment = new PaymentEntity(
                request.customerId(),
                request.amount(),
                request.currency(),
                INITIAL_STATUS,
                Instant.now()
        );

        PaymentEntity savedPayment = paymentRepository.save(payment);

        String paymentXml = paymentXmlMapper.toXml(savedPayment);

        corePaymentClient.send(paymentXml);

        return toResponse(savedPayment);
    }

    public List<PaymentResponse> findPayments(
            String customerId,
            Instant from,
            Instant to
    ) {
        if ((from == null) != (to == null)) {
            throw new InvalidPaymentFilterException(
                    "Both 'from' and 'to' must be provided together"
            );
        }

        if (from.isAfter(to)) {
            throw new InvalidPaymentFilterException(
                    "'from' must be before or equal to 'to'"
            );
        }

        List<PaymentEntity> payments;

        if (customerId != null && from != null) {
            payments = paymentRepository
                    .findByCustomerIdAndCreatedAtBetween(
                            customerId,
                            from,
                            to
                    );
        } else if (customerId != null) {
            payments = paymentRepository.findByCustomerId(customerId);
        } else if (from != null) {
            payments = paymentRepository
                    .findByCreatedAtBetween(from, to);
        } else {
            payments = paymentRepository.findAll();
        }

        return payments.stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(PaymentEntity payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
