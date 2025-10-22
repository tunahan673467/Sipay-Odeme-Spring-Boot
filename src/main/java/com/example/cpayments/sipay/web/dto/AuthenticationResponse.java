package com.example.cpayments.sipay.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    // Kullanıcının korumalı uçlara erişmek için kullanacağı
    // kısa ömürlü (örn: 1 gün) JWT.
    private String accessToken;

    // Kullanıcının, accessToken'ın süresi dolduğunda yeni bir
    // accessToken almak için kullanacağı uzun ömürlü (örn: 7 gün) token.
    private String refreshToken;
}