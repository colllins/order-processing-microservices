package com.collins.orderservice.dto;

import com.collins.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private List<OrderItemResponseDto> items;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;

}
