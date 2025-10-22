package com.example.cpayments.sipay.config;

import com.example.cpayments.sipay.repository.UserRepository;
import lombok.RequiredArgsConstructor; // Ekledik (veya zaten vardı)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Ekledik
import org.springframework.security.crypto.password.PasswordEncoder; // Ekledik

@Configuration
@RequiredArgsConstructor // Ekledik (veya zaten vardı)
public class ApplicationConfig {

    private final UserRepository userRepository;
    // PasswordEncoder field'ı buradan silindi

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash())
                    .roles("USER") // TODO: Rolleri user.getRoles() ile dinamik yap
                    .build();
        };
    }

    // PasswordEncoder Bean'i BURAYA TAŞINDI!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        // Burası değişti: Artık bu sınıftaki passwordEncoder() Bean'ini kullanıyor.
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}