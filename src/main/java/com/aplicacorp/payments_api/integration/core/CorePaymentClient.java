package com.aplicacorp.payments_api.integration.core;

public interface CorePaymentClient {
    void send(String paymentXml);
}
