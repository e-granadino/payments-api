package com.aplicacorp.payments_api.payment.service;

import com.aplicacorp.payments_api.integration.core.CorePaymentClient;
import com.aplicacorp.payments_api.integration.core.PaymentXmlMapper;
import com.aplicacorp.payments_api.payment.dto.CreatePaymentRequest;
import com.aplicacorp.payments_api.payment.dto.PaymentResponse;
import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import com.aplicacorp.payments_api.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentXmlMapper paymentXmlMapper;

    @Mock
    private CorePaymentClient corePaymentClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository,
                paymentXmlMapper,
                corePaymentClient
        );
    }

    @Test
    void shouldCreatePaymentAndSendItToCore() {
        Instant timestamp = Instant.parse("2026-09-19T15:00:00Z");

        CreatePaymentRequest request = new CreatePaymentRequest(
                "CUS-001",
                new BigDecimal("150.75"),
                "USD",
                timestamp
        );

        PaymentEntity savedPayment = new PaymentEntity(
                request.customerId(),
                request.amount(),
                request.currency(),
                "PENDING",
                request.createdAt()
        );

        String paymentXml = """
            <payment>
                <customerId>CUS-001</customerId>
                <amount>150.75</amount>
                <currency>USD</currency>
                <timestamp>2026-09-19T15:00:00Z</timestamp>
            </payment>
            """;

        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(savedPayment);

        when(paymentXmlMapper.toXml(savedPayment))
                .thenReturn(paymentXml);

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response.customerId()).isEqualTo("CUS-001");
        assertThat(response.amount()).isEqualByComparingTo("150.75");
        assertThat(response.currency()).isEqualTo("USD");
        assertThat(response.createdAt()).isEqualTo(timestamp);

        ArgumentCaptor<PaymentEntity> paymentCaptor =
                ArgumentCaptor.forClass(PaymentEntity.class);

        verify(paymentRepository).save(paymentCaptor.capture());

        PaymentEntity persistedPayment = paymentCaptor.getValue();

        assertThat(persistedPayment.getCustomerId())
                .isEqualTo("CUS-001");

        assertThat(persistedPayment.getAmount())
                .isEqualByComparingTo("150.75");

        assertThat(persistedPayment.getCurrency())
                .isEqualTo("USD");

        assertThat(persistedPayment.getCreatedAt())
                .isEqualTo(timestamp);
    }

    @Test
    void shouldPropagateCoreIntegrationFailure() {
        Instant timestamp = Instant.parse("2026-09-19T15:00:00Z");

        CreatePaymentRequest request = new CreatePaymentRequest(
                "CUS-001",
                new BigDecimal("150.75"),
                "USD",

                timestamp
        );

        PaymentEntity savedPayment = new PaymentEntity(
                request.customerId(),
                request.amount(),
                request.currency(),
                "PENDING",
                request.createdAt()
        );

        String paymentXml = "<payment>...</payment>";

        RuntimeException coreException =
                new RuntimeException("Core system unavailable");

        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(savedPayment);

        when(paymentXmlMapper.toXml(savedPayment))
                .thenReturn(paymentXml);

        doThrow(coreException)
                .when(corePaymentClient)
                .send(paymentXml);

        assertThatThrownBy(() -> paymentService.createPayment(request))
                .isSameAs(coreException);

        verify(paymentRepository).save(any(PaymentEntity.class));
        verify(paymentXmlMapper).toXml(savedPayment);
        verify(corePaymentClient).send(paymentXml);

        InOrder inOrder = inOrder(
                paymentRepository,
                paymentXmlMapper,
                corePaymentClient
        );

        inOrder.verify(paymentRepository).save(any(PaymentEntity.class));
        inOrder.verify(paymentXmlMapper).toXml(savedPayment);
        inOrder.verify(corePaymentClient).send(paymentXml);
    }
}