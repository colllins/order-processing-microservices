package com.collins.payment_service.config;

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
    @Value("${order.payment.completed.queue.name}")
    private String order_payment_queue;

    @Value("${notification.payment.completed.queue.name}")
    private String notification_payment_queue;

    @Value("${payment.exchange.name}")
    private String exchange;

    @Value("${order.payment.completed.routing.key}")
    private String order_payment_key;

    @Value("${notification.payment.completed.routing.key}")
    private String notification_payment_key;


    //bean for rabbitmq queue
    @Bean
    public Queue orderPaymentqueue(){
        return new Queue(order_payment_queue);
    }

    @Bean
    public Queue notificationPaymentqueue(){
        return new Queue(notification_payment_queue);
    }

    //bean for exchange
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    //bind queue to exchange w routing key
    @Bean
    public Binding orderPaymentbinding(){
        return BindingBuilder.bind(orderPaymentqueue())
                .to(exchange())
                .with(order_payment_key);
    }

    @Bean
    public Binding notificationPaymentbinding(){
        return BindingBuilder.bind(notificationPaymentqueue())
                .to(exchange())
                .with(notification_payment_key);
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
