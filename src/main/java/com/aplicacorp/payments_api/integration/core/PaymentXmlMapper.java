package com.aplicacorp.payments_api.integration.core;

import com.aplicacorp.payments_api.payment.entity.PaymentEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PaymentXmlMapper {

    private final XmlMapper xmlMapper;

    public PaymentXmlMapper(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    public String toXml(PaymentEntity payment) {
        CorePaymentXml xmlPayment = new CorePaymentXml(
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                DateTimeFormatter.ISO_INSTANT.format(payment.getCreatedAt())
        );

        try {
            return xmlMapper.writeValueAsString(xmlPayment);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to transform payment to XML",
                    exception
            );
        }
    }
}
