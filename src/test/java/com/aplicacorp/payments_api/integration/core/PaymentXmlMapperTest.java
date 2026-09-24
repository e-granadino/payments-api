package com.aplicacorp.payments_api.integration.core;

import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentXmlMapperTest {

    private PaymentXmlMapper paymentXmlMapper;

    @BeforeEach
    void setUp() {
        paymentXmlMapper = new PaymentXmlMapper(new XmlMapper());
    }

    @Test
    void shouldTransformPaymentToXml() {
        Instant timestamp = Instant.parse("2026-09-19T15:00:00Z");

        PaymentEntity payment = new PaymentEntity(
                "CUS-001",
                new BigDecimal("150.75"),
                "USD",
                "PENDING",
                timestamp
        );

        String xml = paymentXmlMapper.toXml(payment);

        assertThat(xml).contains("<payment>");
        assertThat(xml).contains("<customerId>CUS-001</customerId>");
        assertThat(xml).contains("<amount>150.75</amount>");
        assertThat(xml).contains("<currency>USD</currency>");
        assertThat(xml).contains("<createdAt>2026-09-19T15:00:00Z</createdAt>");
        assertThat(xml).contains("</payment>");
    }

    @Test
    void shouldEscapeXmlSpecialCharacters() throws Exception {
        PaymentEntity payment = new PaymentEntity(
                "CUS<&>001",
                new BigDecimal("25.50"),
                "USD",
                "PENDING",
                Instant.parse("2026-09-19T15:00:00Z")
        );

        String xml = paymentXmlMapper.toXml(payment);

        XmlMapper xmlMapper = new XmlMapper();
        CorePaymentXml result = xmlMapper.readValue(xml, CorePaymentXml.class);

        assertThat(result.customerId()).isEqualTo("CUS<&>001");
    }

    @Test
    void shouldPreserveAmountPrecision() {
        PaymentEntity payment = new PaymentEntity(
                "CUS-001",
                new BigDecimal("1234567890123.1234"),
                "USD",
                "PENDING",
                Instant.parse("2026-09-19T15:00:00Z")
        );

        String xml = paymentXmlMapper.toXml(payment);

        assertThat(xml).contains("<amount>1234567890123.1234</amount>");
    }
}