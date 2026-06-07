package com.collins.notification_service.event;

import com.collins.notification_service.entity.PaymentStatus;
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
