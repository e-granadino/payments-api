package com.aplicacorp.payments_api.security;

import com.aplicacorp.payments_api.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldRejectRequestWithoutCredentials() throws Exception {
        mockMvc.perform(
                        get("/api/v1/payments")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "payment-user", roles = "USER")
    void shouldAllowAuthenticatedRequest() throws Exception {
        mockMvc.perform(
                        get("/api/v1/payments")
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowRequestWithValidBasicCredentials() throws Exception {
        mockMvc.perform(
                        get("/api/v1/payments")
                                .with(httpBasic("test-user", "test-password"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectRequestWithInvalidBasicCredentials() throws Exception {
        mockMvc.perform(
                        get("/api/v1/payments")
                                .with(httpBasic("test-user", "wrong-password"))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowOpenApiDocumentationWithoutAuthentication() throws Exception {
        mockMvc.perform(
                        get("/v3/api-docs")
                )
                .andExpect(status().isOk());
    }
}
