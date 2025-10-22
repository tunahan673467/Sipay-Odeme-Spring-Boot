package com.example.cpayments.sipay.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Component: Bu sınıfın bir Spring "bileşeni" olduğunu belirtir.
// Spring, bu sınıfı otomatik olarak bulacak ve 'Filtre Zincirine' ekleyecektir.
@Component
@RequiredArgsConstructor // Gerekli 'final' alanlar için constructor oluşturur.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter, her istek için SADECE BİR KEZ çalışmayı garantiler.

    private final JwtService jwtService;

    // ApplicationConfig'de oluşturduğumuz ve kullanıcıyı
    // veritabanından getirmeyi bilen UserDetailsService'imizi enjekte ediyoruz.
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. İstekten 'Authorization' başlığını (header) al.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Başlık var mı? Ve 'Bearer ' ile mi başlıyor?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Eğer yoksa veya 'Bearer ' değilse, bu isteği es geç.
            // Bu, 'login' veya 'register' gibi halka açık bir istek olabilir.
            // İsteği zincirdeki bir sonraki filtreye (veya hedefe) pasla.
            filterChain.doFilter(request, response);
            return; // Bu filtrenin işi bitti.
        }

        // 3. 'Bearer ' kısmını atıp, sadece token'ın kendisini al.
        jwt = authHeader.substring(7); // "Bearer " (7 karakter)

        // 4. Token'dan kullanıcı adını (email) çıkar.
        userEmail = jwtService.extractUsername(jwt);

        // 5. Kullanıcı adını alabildik Mİ? Ve bu kullanıcı DAHA ÖNCE doğrulanmamış MI?
        // (SecurityContextHolder.getContext().getAuthentication() == null)
        // kontrolü, kullanıcının bu istekte zaten doğrulanmadığından emin olur.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Kullanıcı adıyla veritabanından UserDetails'i çek.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Token geçerli mi? (İmza doğru mu? Süresi dolmamış mı?)
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Eğer token geçerliyse, Spring Security için bir 'Authentication' objesi oluştur.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // JWT kullandığımız için credentials (şifre) 'null' olur.
                        userDetails.getAuthorities()
                );

                // 9. Bu 'Authentication' objesine isteğin detaylarını ekle.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. KULLANICIYI DOĞRULA!
                // Bu 'authToken' objesini 'SecurityContextHolder'a yerleştirmek,
                // Spring'e "Bu kullanıcıyı doğruladım, artık güvenilir." demektir.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. İsteği her durumda zincirdeki bir sonraki filtreye pasla.
        filterChain.doFilter(request, response);
    }
}