package com.collins.notification_service.consumer;

import com.collins.notification_service.dto.CreateNotificationRequestDto;
import com.collins.notification_service.event.PaymentEvent;
import com.collins.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private Logger LOGGER = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final NotificationService notificationService;


    @RabbitListener(queues = "${notification.payment.completed.queue.name}")
    public void consumeMessage(PaymentEvent paymentEvent){
        String title = "Payment "+ paymentEvent.getStatus();
        String message = "Your payment for order #"+paymentEvent.getOrderId()+" was "+paymentEvent.getStatus();

        CreateNotificationRequestDto cnrd = new CreateNotificationRequestDto(
                paymentEvent.getOrderId(),
                paymentEvent.getCustomerEmail(),
                title,
                message
        );
        notificationService.createNotification(cnrd);
    }

}
