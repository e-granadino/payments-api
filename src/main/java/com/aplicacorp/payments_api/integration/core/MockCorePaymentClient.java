package com.aplicacorp.payments_api.integration.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockCorePaymentClient implements CorePaymentClient {

    private static final Logger log =
            LoggerFactory.getLogger(MockCorePaymentClient.class);

    @Override
    public void send(String paymentXml) {
        log.info("Payment sent to core system: {}", paymentXml);
    }
}
