package com.collins.orderservice.controller;

import com.collins.orderservice.dto.CreateOrderRequestDto;
import com.collins.orderservice.dto.OrderResponseDto;
import com.collins.orderservice.entity.OrderStatus;
import com.collins.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OrderController orderController = new OrderController(orderService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();
    }

    @Test
    void getAllOrders_shouldReturn200AndEmptyList() throws Exception {

        // Arrange
        when(orderService.getAllOrders()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getOrderById_shouldReturn404WhenOrderNotFound() throws Exception {

        // Arrange
        Long orderId = 999L;

        when(orderService.getOrderById(orderId))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No Order found with that id"
                ));

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderById_shouldReturn200WhenOrderExists() throws Exception {

        // Arrange
        Long orderId = 1L;

        OrderResponseDto response = new OrderResponseDto(
                orderId,
                "John Doe",
                "john@example.com",
                "1234567890",
                List.of(),
                new BigDecimal("100.00"),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );

        when(orderService.getOrderById(orderId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getAllOrders_shouldReturn200AndOrderList() throws Exception {

        // Arrange
        OrderResponseDto order1 = new OrderResponseDto(
                1L,
                "John Doe",
                "john@example.com",
                "1234567890",
                List.of(),
                new BigDecimal("100.00"),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );

        OrderResponseDto order2 = new OrderResponseDto(
                2L,
                "Jane Doe",
                "jane@example.com",
                "0987654321",
                List.of(),
                new BigDecimal("200.00"),
                OrderStatus.CONFIRMED,
                LocalDateTime.now()
        );

        when(orderService.getAllOrders())
                .thenReturn(List.of(order1, order2));

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[1].customerName").value("Jane Doe"));
    }

    @Test
    void cancelOrder_shouldReturn200AndCancelledStatus() throws Exception {

        // Arrange
        Long orderId = 1L;

        OrderResponseDto response = new OrderResponseDto(
                orderId,
                "John Doe",
                "john@example.com",
                "1234567890",
                List.of(),
                new BigDecimal("100.00"),
                OrderStatus.CANCELLED,
                LocalDateTime.now()
        );

        when(orderService.cancelOrder(orderId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void updateOrderStatus_shouldReturn200AndUpdatedStatus() throws Exception {

        // Arrange
        Long orderId = 1L;

        OrderResponseDto response = new OrderResponseDto(
                orderId,
                "John Doe",
                "john@example.com",
                "1234567890",
                List.of(),
                new BigDecimal("100.00"),
                OrderStatus.CONFIRMED,
                LocalDateTime.now()
        );

        when(orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CONFIRMED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService, times(1))
                .updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    @Test
    void updateOrderStatus_shouldReturn400ForInvalidStatus() throws Exception {

        // Act & Assert
        mockMvc.perform(patch("/api/orders/{orderId}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"INVALID_STATUS\""))
                .andExpect(status().isBadRequest());

        verify(orderService, never())
                .updateOrderStatus(anyLong(), any(OrderStatus.class));
    }

    @Test
    void cancelOrder_shouldReturn400WhenOrderIsCompleted() throws Exception {

        // Arrange
        Long orderId = 1L;

        when(orderService.cancelOrder(orderId))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Completed or cancelled orders cannot be cancelled"
                ));

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void createOrder_shouldReturn200AndCreatedOrder() throws Exception {

        // Arrange
        OrderResponseDto response = new OrderResponseDto(
                1L,
                "John Doe",
                "john@example.com",
                "1234567890",
                List.of(),
                new BigDecimal("100.00"),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );

        when(orderService.createOrder(any(CreateOrderRequestDto.class)))
                .thenReturn(response);

        String requestJson = """
            {
                "customerName": "John Doe",
                "customerEmail": "john@example.com",
                "customerPhone": "1234567890",
                "items": [
                    {
                        "productName": "Keyboard",
                        "quantity": 2,
                        "price": 50.00
                    }
                ]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1))
                .createOrder(any(CreateOrderRequestDto.class));
    }

    @Test
    void createOrder_shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {

        // Arrange
        String requestJson = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(orderService, never())
                .createOrder(any(CreateOrderRequestDto.class));
    }

    @Test
    void createOrder_shouldReturn400WhenItemsListIsEmpty() throws Exception {

        // Arrange
        String requestJson = """
            {
                "customerName": "John Doe",
                "customerEmail": "john@example.com",
                "customerPhone": "1234567890",
                "items": []
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(orderService, never())
                .createOrder(any(CreateOrderRequestDto.class));
    }
}