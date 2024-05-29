package com.gym.gymmicroservice.config;

import com.gym.gymmicroservice.properties.CorsProperties;
import com.gym.gymmicroservice.properties.JwtProperties;
import com.gym.gymmicroservice.security.CustomConverter;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableScheduling
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class SecurityConfig {
    private final CustomConverter converter;

    /**
     * Configures the security filter chain.
     * Disables CSRF protection, configures CORS, sets up URL-based authorization rules, defines session management policy,
     * handles authentication exceptions, configures JWT authentication and decoding, adds custom authentication filter,
     * and sets up logout handling.
     *
     * @param http HttpSecurity object to configure the security filter chain
     * @return SecurityFilterChain object representing the configured security filter chain
     */
    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsProperties corsProperties) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(httpSecurityCorsConfigurer -> {
                UrlBasedCorsConfigurationSource source =
                    new UrlBasedCorsConfigurationSource();
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(corsProperties.getAllowCredentials());
                config.addAllowedOrigin(corsProperties.getAllowedOrigins());
                config.setAllowedHeaders(corsProperties.getAllowedHeaders());
                config.setAllowedMethods(corsProperties.getAllowedMethods());
                source.registerCorsConfiguration("/**", config);
            })
            .authorizeHttpRequests((requests) -> requests
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        return http.build();
    }

    /**
     * Provides a bean for JWT decoding.
     * Uses the JWT secret key to decode JWT tokens.
     *
     * @return JwtDecoder object for decoding JWT tokens
     */
    @Bean
    public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        SecretKey secretKey = new SecretKeySpec(jwtProperties.getKey().getBytes(), Jwts.SIG.HS384.getId());
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS384).build();
    }

    /**
     * Provides a bean for password encryption.
     * Uses BCryptPasswordEncoder for encrypting passwords.
     *
     * @return PasswordEncoder object for encrypting passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
