package com.collins.orderservice.repository;

import com.collins.orderservice.entity.Order;
import com.collins.orderservice.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4");

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveOrder_shouldPersistOrderInMySQL() {

        // Arrange
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john@example.com");
        order.setCustomerPhone("1234567890");
        order.setStatus(OrderStatus.PENDING);

        // Act
        Order savedOrder = orderRepository.saveAndFlush(order);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals("John Doe", savedOrder.getCustomerName());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
    }

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findById_shouldRetrieveOrderDirectlyFromDatabase() {

        // Arrange
        Order order = new Order();
        order.setCustomerName("Jane Doe");
        order.setCustomerEmail("jane@example.com");
        order.setCustomerPhone("1234567890");
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.saveAndFlush(order);
        Long orderId = savedOrder.getId();

        // Clear JPA's first-level cache
        entityManager.clear();

        // Act
        Order retrievedOrder = orderRepository.findById(orderId)
                .orElseThrow();

        // Assert
        assertEquals(orderId, retrievedOrder.getId());
        assertEquals("Jane Doe", retrievedOrder.getCustomerName());
        assertEquals("jane@example.com", retrievedOrder.getCustomerEmail());
    }

    @Test
    void deleteOrder_shouldRemoveOrderFromMySQL() {

        // Arrange
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john@example.com");
        order.setCustomerPhone("1234567890");
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.saveAndFlush(order);
        Long orderId = savedOrder.getId();

        // Act
        orderRepository.deleteById(orderId);
        orderRepository.flush();

        entityManager.clear();

        // Assert
        assertFalse(orderRepository.findById(orderId).isPresent());
    }

    @Test
    void updateOrderStatus_shouldPersistChangesInMySQL() {

        // Arrange
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john@example.com");
        order.setCustomerPhone("1234567890");
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.saveAndFlush(order);
        Long orderId = savedOrder.getId();

        // Act
        savedOrder.setStatus(OrderStatus.CONFIRMED);
        orderRepository.saveAndFlush(savedOrder);

        entityManager.clear();

        // Assert
        Order updatedOrder = orderRepository.findById(orderId)
                .orElseThrow();

        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
    }

    @Test
    void count_shouldReturnCorrectNumberOfOrders() {

        // Arrange
        Order order1 = new Order();
        order1.setCustomerName("John Doe");
        order1.setCustomerEmail("john@example.com");
        order1.setCustomerPhone("1234567890");
        order1.setStatus(OrderStatus.PENDING);

        Order order2 = new Order();
        order2.setCustomerName("Jane Doe");
        order2.setCustomerEmail("jane@example.com");
        order2.setCustomerPhone("0987654321");
        order2.setStatus(OrderStatus.CONFIRMED);

        long initialCount = orderRepository.count();

        orderRepository.saveAndFlush(order1);
        orderRepository.saveAndFlush(order2);

        entityManager.clear();

        // Act
        long finalCount = orderRepository.count();

        // Assert
        assertEquals(initialCount + 2, finalCount);
    }
}