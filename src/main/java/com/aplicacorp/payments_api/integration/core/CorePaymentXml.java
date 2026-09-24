package com.aplicacorp.payments_api.integration.core;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.math.BigDecimal;

@JacksonXmlRootElement(localName = "payment")
public record CorePaymentXml(
        String customerId,
        BigDecimal amount,
        String currency,
        String status,
        String createdAt
) {
}
