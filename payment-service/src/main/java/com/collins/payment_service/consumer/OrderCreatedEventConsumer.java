package com.collins.payment_service.consumer;

import com.collins.payment_service.dto.CreatePaymentRequestDto;
import com.collins.payment_service.event.OrderCreatedEvent;
import com.collins.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);
    private final PaymentService paymentService;

    @RabbitListener(queues = {"${payment.order.created.queue.name}"})
    public void consumeMessage(OrderCreatedEvent orderCreatedEvent){
        CreatePaymentRequestDto cprd = new CreatePaymentRequestDto(
                orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getCustomerEmail(),
                orderCreatedEvent.getTotal()
        );

        paymentService.createPayment(cprd);

    }
}
