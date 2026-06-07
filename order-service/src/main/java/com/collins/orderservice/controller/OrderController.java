package com.collins.orderservice.controller;

import com.collins.orderservice.dto.CreateOrderRequestDto;
import com.collins.orderservice.dto.OrderResponseDto;
import com.collins.orderservice.entity.OrderStatus;
import com.collins.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto createOrder(@Valid @RequestBody CreateOrderRequestDto cord){
        return orderService.createOrder(cord);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long orderId){
        return orderService.getOrderById(orderId);
    }

    @GetMapping
    public List<OrderResponseDto> getAllOrders(){
        return orderService.getAllOrders();
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus orderStatus){
        return orderService.updateOrderStatus(orderId, orderStatus);
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponseDto cancelOrder(@PathVariable Long orderId){
        return orderService.cancelOrder(orderId);
    }
}
