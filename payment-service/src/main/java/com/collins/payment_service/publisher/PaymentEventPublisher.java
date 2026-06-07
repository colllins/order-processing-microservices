package com.collins.payment_service.publisher;

import com.collins.payment_service.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
/*
notification.payment.completed.queue.name=notification.payment.completed.queue
notification.payment.completed.routing.key=notification.payment.completed.key

order.payment.completed.queue.name=order.payment.completed.queue
payment.exchange.name=payment.exchange
order.payment.completed.routing.key=order.payment.completed.key
 */
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {
    @Value("${payment.exchange.name}")
    private String exchange;

    @Value("${order.payment.completed.routing.key}")
    private String orderPaymentKey;

    @Value("${notification.payment.completed.routing.key}")
    private String notificationPaymentKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(PaymentEvent paymentCompletedEvent){
        rabbitTemplate.convertAndSend(exchange, orderPaymentKey, paymentCompletedEvent);
        rabbitTemplate.convertAndSend(exchange, notificationPaymentKey, paymentCompletedEvent);
        LOGGER.info(String.format("Json message sent -> %s", paymentCompletedEvent.toString()));
    }
}
