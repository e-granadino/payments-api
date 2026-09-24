package com.aplicacorp.payments_api.common.dto;

public record PaymentApiResponse<T>(
        boolean success,
        T data
) {

    public static <T> PaymentApiResponse<T> success(T data) {
        return new PaymentApiResponse<>(true, data);
    }
}
