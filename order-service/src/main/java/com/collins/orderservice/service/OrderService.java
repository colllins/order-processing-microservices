package com.collins.orderservice.service;

import com.collins.orderservice.dto.CreateOrderRequestDto;
import com.collins.orderservice.dto.OrderItemResponseDto;
import com.collins.orderservice.dto.OrderResponseDto;
import com.collins.orderservice.entity.Order;
import com.collins.orderservice.entity.OrderItem;
import com.collins.orderservice.entity.OrderStatus;
import com.collins.orderservice.event.OrderCreatedEvent;
import com.collins.orderservice.publisher.OrderEventPublisher;
import com.collins.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    private OrderResponseDto mapToOrderResponseDto(Order ord) {
        List<OrderItemResponseDto> itemDtos = ord.getItems()
                .stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderResponseDto(
                ord.getId(),
                ord.getCustomerName(),
                ord.getCustomerEmail(),
                ord.getCustomerPhone(),
                itemDtos,
                ord.getTotal(),
                ord.getStatus(),
                ord.getCreatedAt()
        );
    }

    public OrderResponseDto createOrder(CreateOrderRequestDto cord) {
        BigDecimal total = BigDecimal.ZERO;

        List<OrderItem> items = cord.getItems();
        for (int i = 0; i < items.size(); i++) {
            int qty = items.get(i).getQuantity();
            BigDecimal price = items.get(i).getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);
        }

        OrderStatus status = OrderStatus.PENDING;
        Order order = new Order();
        order.setCustomerName(cord.getCustomerName());
        order.setCustomerEmail(cord.getCustomerEmail());
        order.setCustomerPhone(cord.getCustomerPhone());
        order.setItems(items);
        items.forEach(item -> item.setOrder(order));
        order.setTotal(total);
        order.setStatus(status);


        Order orderResponse = orderRepository.save(order);

        /**
         * this is for OrderCreatedEvent message to be published
         *     private Long orderId;
         *     private String customerName;
         *     private String customerEmail;
         *     private BigDecimal total;
         *     private LocalDateTime createdAt;
         * **/
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderResponse.getId(),
                orderResponse.getCustomerName(),
                orderResponse.getCustomerEmail(),
                orderResponse.getTotal(),
                orderResponse.getCreatedAt()
        );
        orderEventPublisher.sendMessage(orderCreatedEvent);

        return mapToOrderResponseDto(orderResponse);
    }

    public OrderResponseDto getOrderById(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Order found with that id"));
            return mapToOrderResponseDto(order);
    }

    public List<OrderResponseDto> getAllOrders(){
        List<OrderResponseDto> list = new ArrayList<>();
        orderRepository.findAll().forEach(order ->
               list.add(new OrderResponseDto(
                        order.getId(),
                        order.getCustomerName(),
                        order.getCustomerEmail(),
                        order.getCustomerPhone(),
                        order.getItems()
                                .stream()
                                .map(item -> new OrderItemResponseDto(
                                        item.getId(),
                                        item.getProductName(),
                                        item.getQuantity(),
                                        item.getPrice()
                                ))
                                .toList(),
                        order.getTotal(),
                        order.getStatus(),
                        order.getCreatedAt()))
                );

        return list;
    }

    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus orderStatus){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Order found w that id"));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed or cancelled orders cannot be updated"
            );
        }else{
            order.setStatus(orderStatus);
        }

        Order orderResponse = orderRepository.save(order);
        return mapToOrderResponseDto(orderResponse);
    }

    public OrderResponseDto cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Order found w that id"));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed or cancelled orders cannot be cancelled"
            );
        }else{
            order.setStatus(OrderStatus.CANCELLED);
        }

        Order orderResponse = orderRepository.save(order);
        return mapToOrderResponseDto(orderResponse);
    }

}
