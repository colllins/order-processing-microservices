package com.collins.payment_service.controller;

import com.collins.payment_service.dto.CreatePaymentRequestDto;
import com.collins.payment_service.dto.PaymentResponseDto;
import com.collins.payment_service.entity.PaymentStatus;
import com.collins.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponseDto createPayment(@RequestBody @Valid CreatePaymentRequestDto cprd){
        return paymentService.createPayment(cprd);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDto getPaymentById(@PathVariable Long paymentId){
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponseDto getPaymentByOrderId(@PathVariable Long orderId){
        return paymentService.getPaymentByOrderId(orderId);
    }

    @PatchMapping("/{paymentId}/status")
    public PaymentResponseDto updatePaymentStatus(@PathVariable Long paymentId, @RequestBody PaymentStatus status){
        return paymentService.updatePaymentStatus(paymentId, status);
    }
}
