package com.collins.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUserRequestDto {

    @NotBlank
    @Email
    @Size(max = 254)
    private  String email;

    @NotBlank
    @Size(max = 60)
    private String password;

    @NotNull
    private String role;
}
