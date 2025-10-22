package com.example.cpayments.sipay.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {


    @NotBlank(message = "Email cannot be blank")
    // @Email: Bu alanın geçerli bir e-posta formatında olmasını zorunlu kılar.
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    // @Size: Şifrenin minimum ve maksimum uzunluğunu belirler.
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    private String password;
}