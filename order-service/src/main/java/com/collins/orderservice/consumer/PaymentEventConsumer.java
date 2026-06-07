package com.collins.orderservice.consumer;

import com.collins.orderservice.entity.OrderStatus;
import com.collins.orderservice.entity.PaymentStatus;
import com.collins.orderservice.event.PaymentEvent;
import com.collins.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final OrderService orderService;

    @RabbitListener(queues = {"${order.payment.completed.queue.name}"})
    public void consumeMessage(PaymentEvent paymentEvent){
        if(paymentEvent.getStatus() == PaymentStatus.SUCCESS) {
            orderService.updateOrderStatus(paymentEvent.getOrderId(), OrderStatus.CONFIRMED);
        }else if(paymentEvent.getStatus() == PaymentStatus.FAILED){
            orderService.updateOrderStatus(paymentEvent.getOrderId(), OrderStatus.PAYMENT_FAILED);
        }
    }
}
