package com.collins.orderservice.dto;

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
public class OrderItemRequestDto {
    @NotNull
    private String productName;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal price;
}
