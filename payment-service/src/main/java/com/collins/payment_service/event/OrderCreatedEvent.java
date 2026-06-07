package com.collins.payment_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class OrderCreatedEvent {
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private BigDecimal total;
    private LocalDateTime createdAt;
}
