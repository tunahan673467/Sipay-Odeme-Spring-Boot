package com.example.cpayments.sipay.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserController {

    /**
     * GET /api/me
     * Bu uç, SecurityConfig'de ".anyRequest().authenticated()" kuralı tarafından
     * koruma altına alınmıştır. Sadece geçerli bir JWT'ye sahip kullanıcılar
     * bu metoda erişebilir.
     *
     * @param authentication Spring Security tarafından, JwtAuthenticationFilter'da
     * doğrulanan ve SecurityContext'e yerleştirilen
     * kullanıcı kimlik bilgisini temsil eder.
     * @return Başarılı olursa, doğrulanan kullanıcının email adresini döner.
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile(Authentication authentication) {

        // Eğer istek bu metoda kadar gelebildiyse, 'authentication' objesinin
        // 'null' olmayacağı garanti edilir (çünkü filtre ve güvenlik zinciri
        // geçersiz isteği çoktan reddetmiştir).

        // authentication.getName() metodu, UserDetails'in 'username' alanını
        // (bizim için 'email' idi) döner.
        String userEmail = authentication.getName();

        return ResponseEntity.ok("Başarıyla doğrulandınız. Giriş yapan kullanıcı: " + userEmail);
    }

    /*
     // Alternatif ve daha basit bir yöntem (Principal kullanmak):
     @GetMapping("/me")
     public ResponseEntity<String> getMyProfile(Principal principal) {
        // 'principal.getName()' de bize 'username' (email) alanını döner.
        return ResponseEntity.ok("Başarıyla doğrulandınız. Giriş yapan kullanıcı: " + principal.getName());
     }
    */
}