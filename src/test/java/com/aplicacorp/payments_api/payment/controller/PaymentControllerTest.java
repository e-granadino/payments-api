package com.aplicacorp.payments_api.payment.controller;

import com.aplicacorp.payments_api.common.exception.InvalidPaymentFilterException;
import com.aplicacorp.payments_api.payment.dto.CreatePaymentRequest;
import com.aplicacorp.payments_api.payment.dto.PaymentResponse;
import com.aplicacorp.payments_api.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldCreatePayment() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");

        PaymentResponse response = new PaymentResponse(
                paymentId,
                "CUS-001",
                new BigDecimal("150.75"),
                "USD",
                "PENDING",
                createdAt
        );

        when(paymentService.createPayment(any(CreatePaymentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "customerId": "CUS-001",
                        "amount": 150.75,
                        "currency": "USD"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.data.customerId").value("CUS-001"))
                .andExpect(jsonPath("$.data.amount").value(150.75))
                .andExpect(jsonPath("$.data.currency").value("USD"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.createdAt").value("2026-09-23T10:00:00Z"));

        verify(paymentService).createPayment(any(CreatePaymentRequest.class));
    }

    @Test
    void shouldReturnPaymentsFilteredByDateRange() throws Exception {
        Instant from = Instant.parse("2026-09-01T00:00:00Z");
        Instant to = Instant.parse("2026-09-23T23:59:59Z");

        UUID paymentId = UUID.randomUUID();

        PaymentResponse response = new PaymentResponse(
                paymentId,
                "CUS-002",
                new BigDecimal("500.00"),
                "USD",
                "PROCESSED",
                Instant.parse("2026-09-15T12:00:00Z")
        );

        when(paymentService.findPayments(
                null,
                from,
                to
        )).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/payments")
                        .param("from", "2026-09-01T00:00:00Z")
                        .param("to", "2026-09-23T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].customerId").value("CUS-002"))
                .andExpect(jsonPath("$.data[0].status").value("PROCESSED"));

        verify(paymentService).findPayments(
                null,
                from,
                to
        );
    }

    @Test
    void shouldReturnPaymentsFilteredByCustomerAndDateRange() throws Exception {
        Instant from = Instant.parse("2026-09-01T00:00:00Z");
        Instant to = Instant.parse("2026-09-23T23:59:59Z");

        UUID paymentId = UUID.randomUUID();

        PaymentResponse response = new PaymentResponse(
                paymentId,
                "CUS-003",
                new BigDecimal("750.50"),
                "USD",
                "PROCESSED",
                Instant.parse("2026-09-10T15:30:00Z")
        );

        when(paymentService.findPayments(
                "CUS-003",
                from,
                to
        )).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/payments")
                        .param("customerId", "CUS-003")
                        .param("from", "2026-09-01T00:00:00Z")
                        .param("to", "2026-09-23T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].customerId").value("CUS-003"))
                .andExpect(jsonPath("$.data[0].status").value("PROCESSED"));

        verify(paymentService).findPayments(
                "CUS-003",
                from,
                to
        );
    }

    @Test
    void shouldReturnBadRequestWhenCreatePaymentRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "customerId": "",
                        "amount": 0,
                        "currency": "usd"
                    }
                    """))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturnBadRequestWhenDateRangeIsInvalid() throws Exception {
//        when(paymentService.findPayments(
        Instant from = Instant.parse("2026-09-23T23:59:59Z");
        Instant to = Instant.parse("2026-09-01T00:00:00Z");

        when(paymentService.findPayments("CUS-001", from, to))
                .thenThrow(
                        new InvalidPaymentFilterException(
                                "'from' must be before or equal to 'to'"
                        )
                );

        mockMvc.perform(get("/api/v1/payments")
                        .param("customerId", "CUS-001")
                        .param("from", "2026-09-23T23:59:59Z")
                        .param("to", "2026-09-01T00:00:00Z"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOnlyOneDateFilterIsProvided() throws Exception {
        mockMvc.perform(get("/api/v1/payments")
                        .param("from", "2026-09-01T00:00:00Z"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "customerId": "CUS-001",
                        "amount":
                    }
                    """))
                .andExpect(status().isBadRequest());
    }
}