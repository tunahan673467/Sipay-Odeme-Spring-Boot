package com.example.cpayments.sipay.web;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController anotasyonu, Spring'e bu sınıfın dışarıdan gelen web isteklerini
// karşılamakla görevli bir "Controller" olduğunu söyler. Ayrıca, bu sınıftaki
// metotların döndürdüğü değerlerin doğrudan HTTP cevabının gövdesine (body)
// yazılacağını belirtir. Yani bir web sayfası (view) değil, direkt veri (JSON, text vb.) döner.
@RestController
// @RequestMapping("/api") anotasyonu, bu sınıfın içindeki tüm API uçlarının
// "/api" ön ekiyle başlayacağını belirtir.
@RequestMapping("/api")
public class PingController {

    // @GetMapping("/ping") anotasyonu, HTTP GET metodu ile "/api/ping" adresine
    // gelen isteklerin bu metot tarafından karşılanacağını belirtir.
    @GetMapping("/ping")
    public String ping() {
        // Metot, basit bir String olan "pong" ifadesini döner.
        // @RestController sayesinde bu String, HTTP cevabı olarak istemciye gönderilir.
        return "pong";
    }
}