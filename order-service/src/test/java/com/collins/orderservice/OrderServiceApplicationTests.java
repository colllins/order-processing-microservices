package com.collins.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(properties = {
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"spring.rabbitmq.listener.simple.auto-startup=false",
		"payment.order.created.queue.name=test-order-created-queue",
		"order.exchange.name=test-order-exchange",
		"payment.order.created.routing.key=test-order-created",
		"notification.order.created.queue.name=test-notification-order-created",
		"notification.order.created.routing.key=test-notification-order-created",
		"order.payment.completed.queue.name=test-payment-completed-queue"
})
class OrderServiceApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer<?> mysql =
			new MySQLContainer<>("mysql:8.4");

	@Test
	void contextLoads() {
	}
}