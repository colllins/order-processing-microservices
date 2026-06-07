package com.collins.orderservice.publisher;

import com.collins.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    @Value("${order.exchange.name}")
    private String exchange;

    @Value("${notification.order.created.routing.key}")
    private String notification_order_key;

    @Value("${payment.order.created.routing.key}")
    private String payment_order_key;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(OrderCreatedEvent orderCreatedEvent){

        //send order created event to payment_order_queue
        rabbitTemplate.convertAndSend(exchange, payment_order_key, orderCreatedEvent);

        //send order created event to notififcation_order_queue
        rabbitTemplate.convertAndSend(exchange, notification_order_key, orderCreatedEvent);

        LOGGER.info(String.format("Json message sent -> %s", orderCreatedEvent.toString()));
    }
}
