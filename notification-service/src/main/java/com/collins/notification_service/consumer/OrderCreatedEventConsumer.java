package com.collins.notification_service.consumer;

import com.collins.notification_service.dto.CreateNotificationRequestDto;
import com.collins.notification_service.event.OrderCreatedEvent;
import com.collins.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private Logger LOGGER = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);
    private final NotificationService notificationService;

    @RabbitListener(queues = "${notification.order.created.queue.name}")
    public void consumeMessage(OrderCreatedEvent orderCreatedEvent){

        String title = "Order Received";
        String message = title+": Your order #"+ orderCreatedEvent.getOrderId()+ " has been received" +
                " Status: PENDING";
        CreateNotificationRequestDto cnrd = new CreateNotificationRequestDto(
                orderCreatedEvent.getOrderId(),
                orderCreatedEvent.getCustomerEmail(),
                title,
                message
        );
        notificationService.createNotification(cnrd);

    }

}
