package com.collins.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${payment.order.created.queue.name}")
    private String payment_order_queue;

    @Value("${order.exchange.name}")
    private String exchange;

    @Value("${payment.order.created.routing.key}")
    private String payment_order_key;

    @Value("${notification.order.created.queue.name}")
    private String notification_order_queue;

    @Value("${notification.order.created.routing.key}")
    private String notification_order_key;

    /*
    bean for payment order queue
    when an order event is created, it goes to this queue so a payment consumer can consume it
     */
    @Bean
    public Queue paymentOrderqueue(){
        return new Queue(payment_order_queue);
    }

    /*
    bean for notification order queue
    when an order event is created, it goes to this queue so a notification consumer can consume it
 */
    @Bean
    public Queue notificationOrderqueue(){
        return new Queue(notification_order_queue);
    }

    //bean for exchange
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    /*
    binds exchange to payment_order queue with payment_order_key
     */
    @Bean
    public Binding paymentOrderBinding(){
        return BindingBuilder.bind(paymentOrderqueue())
                .to(exchange())
                .with(payment_order_key);
    }

    /*
    binds exchange to notification_order queue with notification_order_key
     */
    @Bean
    public Binding notificationOrderBinding(){
        return BindingBuilder.bind(notificationOrderqueue())
                .to(exchange())
                .with(notification_order_key);
    }


    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
