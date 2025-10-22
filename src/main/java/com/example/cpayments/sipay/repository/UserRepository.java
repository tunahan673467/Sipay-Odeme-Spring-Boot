package com.example.cpayments.sipay.repository;

import com.example.cpayments.sipay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository anotasyonu, Spring'e bu arayüzün bir veritabanı işlem
// bileşeni (Bean) olduğunu belirtir. Spring Data JPA için bu zorunlu olmasa da
// kodun okunabilirliğini artırır.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA'nın isimlendirme gücü sayesinde, metot adını belirli bir
    // kurala göre yazdığımızda (findBy + AlanAdı), Spring arkaplanda bizim için
    // SQL sorgusunu otomatik olarak oluşturur.
    // Bu metot, "SELECT * FROM users WHERE email = ?" sorgusunu çalıştıracaktır.
    //
    // Optional<User> kullanmamızın sebebi: Aradığımız email adresi veritabanında
    // olmayabilir. Optional, bu "yokluk" durumunu (null yerine) daha güvenli
    // bir şekilde yönetmemizi sağlar.
    Optional<User> findByEmail(String email);
}