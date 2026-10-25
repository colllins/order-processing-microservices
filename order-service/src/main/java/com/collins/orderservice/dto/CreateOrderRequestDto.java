package com.collins.orderservice.dto;

import com.collins.orderservice.entity.OrderItem;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderRequestDto {

    @NotBlank
    private String customerName;

    @NotBlank
    @Email
    private String customerEmail;

    @NotNull
    private String customerPhone;

    @NotEmpty
    private List<OrderItem> items;
}
