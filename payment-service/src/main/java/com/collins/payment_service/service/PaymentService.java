package com.collins.payment_service.service;

import com.collins.payment_service.dto.CreatePaymentRequestDto;
import com.collins.payment_service.dto.PaymentResponseDto;
import com.collins.payment_service.entity.Payment;
import com.collins.payment_service.entity.PaymentStatus;
import com.collins.payment_service.event.PaymentEvent;
import com.collins.payment_service.publisher.PaymentEventPublisher;
import com.collins.payment_service.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    private boolean paymentOutCome(){
        Random random = new Random();
        return random.nextBoolean();
    }

    public PaymentResponseDto createPayment(CreatePaymentRequestDto cprd){
        Payment payment = new Payment();
        Random random = new Random();

        payment.setOrderId(cprd.getOrderId());
        payment.setAmount(cprd.getAmount());
        if (random.nextBoolean()) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment.setCustomerEmail(cprd.getCustomerEmail());

        Payment paymentResponse = paymentRepository.save(payment);

        /**
         * create payment completed event message and publish it
         */
        PaymentEvent paymentCompletedEvent = new PaymentEvent(
                paymentResponse.getId(),
                paymentResponse.getOrderId(),
                paymentResponse.getCustomerEmail(),
                paymentResponse.getAmount(),
                paymentResponse.getStatus(),
                paymentResponse.getCreatedAt()
        );

        paymentEventPublisher.sendMessage(paymentCompletedEvent);



        return new PaymentResponseDto(
                paymentResponse.getId(),
                paymentResponse.getOrderId(),
                paymentResponse.getAmount(),
                paymentResponse.getStatus(),
                paymentResponse.getCreatedAt(),
                paymentResponse.getUpdatedAt()
        );
    }

    public PaymentResponseDto getPaymentById(Long paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Payment found with that id"));

        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public PaymentResponseDto getPaymentByOrderId(Long orderId){
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Payment found with that order id"));

        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public PaymentResponseDto updatePaymentStatus(Long paymentId, PaymentStatus status){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Payment found with that id"));

        if(payment.getStatus()==PaymentStatus.SUCCESS || payment.getStatus()==PaymentStatus.FAILED){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Successful or Failed payments cannot be updated"
            );
        }else{
            payment.setStatus(status);
        }

        Payment paymentResponse = paymentRepository.save(payment);

        return new PaymentResponseDto(
                paymentResponse.getId(),
                paymentResponse.getOrderId(),
                paymentResponse.getAmount(),
                paymentResponse.getStatus(),
                paymentResponse.getCreatedAt(),
                paymentResponse.getUpdatedAt()
        );
    }
}
