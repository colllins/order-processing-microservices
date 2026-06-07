package com.collins.payment_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentRequestDto {

    @NotNull
    private Long orderId;

    @NotNull
    private String customerEmail;

    @NotNull
    private BigDecimal amount;
}
