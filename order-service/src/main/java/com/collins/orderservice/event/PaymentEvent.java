package com.collins.orderservice.event;

import com.collins.orderservice.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class PaymentEvent {
    private Long paymentId;
    private Long orderId;
    private String customerEmail;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
