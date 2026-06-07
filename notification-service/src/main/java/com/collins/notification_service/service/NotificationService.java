package com.collins.notification_service.service;

import com.collins.notification_service.dto.CreateNotificationRequestDto;
import com.collins.notification_service.dto.NotificationResponseDto;
import com.collins.notification_service.entity.Notification;
import com.collins.notification_service.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationResponseDto createNotification(CreateNotificationRequestDto cnrd){
        Notification notification = new Notification();
        notification.setOrderId(cnrd.getOrderId());
        notification.setCustomerEmail(cnrd.getCustomerEmail());
        notification.setTitle(cnrd.getTitle());
        notification.setMessage(cnrd.getMessage());

        Notification notificationResponse = notificationRepository.save(notification);

        return new NotificationResponseDto(
                notificationResponse.getId(),
                notificationResponse.getOrderId(),
                notificationResponse.getCustomerEmail(),
                notificationResponse.getTitle(),
                notificationResponse.getMessage(),
                notificationResponse.isRead(),
                notificationResponse.getCreatedAt()
        );
    }

    public NotificationResponseDto getNotificationById(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Notification found with that id"));


        return new NotificationResponseDto(
                notification.getId(),
                notification.getOrderId(),
                notification.getCustomerEmail(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    public List<NotificationResponseDto> markAllAsRead(){
       List<Notification> list = new ArrayList<>();
               notificationRepository.findAll()
               .forEach(notification -> {
                   if(!notification.isRead()){
                       notification.setRead(true);
                       notificationRepository.save(notification);
                       list.add(notification);
                   }
               });
     return   list
               .stream()
               .map(notification -> new NotificationResponseDto(
                       notification.getId(),
                       notification.getOrderId(),
                       notification.getCustomerEmail(),
                       notification.getTitle(),
                       notification.getMessage(),
                       notification.isRead(),
                       notification.getCreatedAt()
               )).toList();
    }

    public List<NotificationResponseDto> getAllNotifications(){
     return    notificationRepository.findAll()
                .stream()
                .map(notification -> new NotificationResponseDto(
                        notification.getId(),
                        notification.getOrderId(),
                        notification.getCustomerEmail(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.isRead(),
                        notification.getCreatedAt()
                )).toList();
    }

    public NotificationResponseDto markNotificationAsRead(Long notificationId){

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Notification found with that id"));

        if(notification.isRead()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Notification has already been read"
            );
        }

            notification.setRead(true);
            Notification notificationResponse = notificationRepository.save(notification);


            return new NotificationResponseDto(
                    notificationResponse.getId(),
                    notificationResponse.getOrderId(),
                    notificationResponse.getCustomerEmail(),
                    notificationResponse.getTitle(),
                    notificationResponse.getMessage(),
                    notificationResponse.isRead(),
                    notificationResponse.getCreatedAt()
            );

        }

}
