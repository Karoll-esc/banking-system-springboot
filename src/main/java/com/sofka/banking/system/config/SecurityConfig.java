package com.sofka.banking.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para la aplicación bancaria.
 *
 * Esta configuración: - Proporciona un PasswordEncoder con BCrypt para hashear contraseñas -
 * Deshabilita la seguridad por defecto permitiendo acceso a todos los endpoints (se configurará
 * autenticación/autorización más adelante)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean de PasswordEncoder usando BCrypt. BCrypt es un algoritmo de hash adaptativo que incluye
     * un salt aleatorio y es resistente a ataques de fuerza bruta.
     *
     * @return BCryptPasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de la cadena de filtros de seguridad.
     *
     * Deshabilita temporalmente la seguridad para todos los endpoints. Esto permite desarrollo sin
     * autenticación.
     *
     * IMPORTANTE: En producción, habilitar autenticación y autorización apropiadas.
     *
     * @param http HttpSecurity para configurar
     * @return SecurityFilterChain configurado
     * @throws Exception si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll() // Permitir todas las
                                                                             // peticiones
                ).headers(headers -> headers.frameOptions(frame -> frame.disable()) // Permitir
                                                                                    // frames para
                                                                                    // H2 console
                );

        return http.build();
    }
}
