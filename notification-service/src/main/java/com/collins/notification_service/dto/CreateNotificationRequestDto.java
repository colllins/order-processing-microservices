package com.collins.notification_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateNotificationRequestDto {
    @NotNull
    private Long orderId;
    @NotNull
    private String customerEmail;
    @NotNull
    private String title;

    @NotNull
    private String message;
}
