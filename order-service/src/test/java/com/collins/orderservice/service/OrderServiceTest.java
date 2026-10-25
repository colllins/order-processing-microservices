package com.collins.orderservice.service;

import com.collins.orderservice.dto.CreateOrderRequestDto;
import com.collins.orderservice.dto.OrderResponseDto;
import com.collins.orderservice.entity.Order;
import com.collins.orderservice.entity.OrderItem;
import com.collins.orderservice.entity.OrderStatus;
import com.collins.orderservice.event.OrderCreatedEvent;
import com.collins.orderservice.publisher.OrderEventPublisher;
import com.collins.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldSetStatusToPending() {
        // Arrange: Create a sample order item
        OrderItem item = new OrderItem();
        item.setProductName("Keyboard");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("50.00"));

        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setCustomerName("Martin");
        request.setCustomerEmail("test@example.com");
        request.setCustomerPhone("6145551234");
        request.setItems(List.of(item));

        // Mock the repository to return the saved order
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the actual service method
        OrderResponseDto response = orderService.createOrder(request);

        // Assert: Verify the result
        assertEquals(OrderStatus.PENDING, response.getStatus());

    }

    @Test
    void createOrder_shouldCalculateTotalCorrectly() {

        // Arrange
        OrderItem item1 = new OrderItem();
        item1.setProductName("Keyboard");
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("50.00"));

        OrderItem item2 = new OrderItem();
        item2.setProductName("Mouse");
        item2.setQuantity(3);
        item2.setPrice(new BigDecimal("20.00"));

        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setCustomerName("Martin");
        request.setCustomerEmail("test@example.com");
        request.setCustomerPhone("6145551234");
        request.setItems(List.of(item1, item2));

        // Mock repository
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponseDto response = orderService.createOrder(request);

        // Assert
        assertEquals(
                0,
                new BigDecimal("160.00").compareTo(response.getTotal())
        );
    }

    @Test
    void createOrder_shouldPublishOrderCreatedEvent() {

        // Arrange
        OrderItem item = new OrderItem();
        item.setProductName("Keyboard");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("50.00"));

        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setCustomerName("Martin");
        request.setCustomerEmail("test@example.com");
        request.setCustomerPhone("6145551234");
        request.setItems(List.of(item));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.createOrder(request);

        // Assert
        verify(orderEventPublisher, times(1))
                .sendMessage(any(OrderCreatedEvent.class));
    }

    @Test
    void getOrderById_shouldReturnOrderWhenFound() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setCustomerName("Martin");
        order.setCustomerEmail("test@example.com");
        order.setCustomerPhone("6145551234");
        order.setItems(List.of());
        order.setTotal(new BigDecimal("100.00"));
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        OrderResponseDto response = orderService.getOrderById(orderId);

        // Assert
        assertEquals(orderId, response.getId());
        assertEquals("Martin", response.getCustomerName());
        assertEquals(OrderStatus.PENDING, response.getStatus());
    }

    @Test
    void getOrderById_shouldThrowExceptionWhenNotFound() {

        // Arrange
        Long orderId = 999L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.getOrderById(orderId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {

        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        order1.setCustomerName("Martin");
        order1.setItems(List.of());
        order1.setStatus(OrderStatus.PENDING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerName("Maggie");
        order2.setItems(List.of());
        order2.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findAll())
                .thenReturn(List.of(order1, order2));

        // Act
        List<OrderResponseDto> responses = orderService.getAllOrders();

        // Assert
        assertEquals(2, responses.size());
        assertEquals("Martin", responses.get(0).getCustomerName());
        assertEquals("Maggie", responses.get(1).getCustomerName());
    }

    @Test
    void getAllOrders_shouldReturnEmptyListWhenNoOrdersExist() {

        // Arrange
        when(orderRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<OrderResponseDto> responses = orderService.getAllOrders();

        // Assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void updateOrderStatus_shouldUpdateStatusSuccessfully() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(List.of());

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponseDto response =
                orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);

        // Assert
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatus_shouldRejectCancelledOrder() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.updateOrderStatus(
                        orderId, OrderStatus.CONFIRMED)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_shouldRejectCompletedOrder() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.updateOrderStatus(
                        orderId, OrderStatus.CONFIRMED)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_shouldCancelPendingOrder() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(List.of());

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponseDto response = orderService.cancelOrder(orderId);

        // Assert
        assertEquals(OrderStatus.CANCELLED, response.getStatus());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void cancelOrder_shouldRejectAlreadyCancelledOrder() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.cancelOrder(orderId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(orderRepository, never()).save(any(Order.class));
    }
}