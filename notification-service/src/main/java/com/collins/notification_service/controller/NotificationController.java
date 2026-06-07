package com.collins.notification_service.controller;

import com.collins.notification_service.dto.CreateNotificationRequestDto;
import com.collins.notification_service.dto.NotificationResponseDto;
import com.collins.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public NotificationResponseDto createNotification(@Valid @RequestBody CreateNotificationRequestDto cnrd){
        return notificationService.createNotification(cnrd);
    }

    @GetMapping("/{notificationId}")
    public NotificationResponseDto getNotificationById(@PathVariable Long notificationId){
        return notificationService.getNotificationById(notificationId);
    }

    @GetMapping()
    public List<NotificationResponseDto> getAllNotifications(){
        return notificationService.getAllNotifications();
    }

    @PostMapping("/{notificationId}/mark-as-read")
    public NotificationResponseDto markNotificationAsRead(@PathVariable Long notificationId){
        return notificationService.markNotificationAsRead(notificationId);
    }

    @PostMapping("/mark-all-as-read")
    public List<NotificationResponseDto> markAllNotificationsAsRead(){
        return notificationService.markAllAsRead();
    }
}
