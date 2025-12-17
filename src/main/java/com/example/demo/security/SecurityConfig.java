package com.example.demo.security;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless JWT
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults()) // QUAN TRỌNG
            // Stateless session, không dùng session của Spring Security
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Phân quyền các endpoint
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws/**").permitAll()
                // Public: đăng ký & đăng nhập
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/payments/confirm-webhook").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/uploads/**").permitAll() // Cho phép truy cập tệp tải lên

                // Public: xem danh sách + chi tiết sản phẩm
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/tree").permitAll()

                // Seller (hoặc Admin) mới được tạo/ sửa/ xóa sản phẩm
                .requestMatchers(HttpMethod.POST,   "/api/products").hasAnyRole("SELLER", "ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/products/**").hasAnyRole("SELLER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("SELLER", "ADMIN")

                // Customer (hoặc Seller, Admin) mới được thao tác giỏ hàng, đặt hàng
                .requestMatchers("/api/carts/**").hasAnyRole("CUSTOMER","SELLER", "ADMIN")
                .requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER","SELLER", "ADMIN")

                // SELLER (hoặc Admin) mới được thao tác với trạng thái đơn hàng
                .requestMatchers("/api/ordersworkflow/**").hasAnyRole("SELLER", "ADMIN")

                // Admin mới được truy cập các API quản trị
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/categories/**").hasRole("ADMIN")

                // Mọi request khác phải xác thực
                .anyRequest().authenticated()
            )
            // Chèn JWT filter trước filter mặc định
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://10.24.3.254:5173"
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With","Accept"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization","Location","X-Total-Count"));
        config.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
