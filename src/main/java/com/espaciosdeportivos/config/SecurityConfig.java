package com.espaciosdeportivos.config;

import com.espaciosdeportivos.security.JwtAuthenticationEntryPoint;
import com.espaciosdeportivos.security.JwtAuthenticationFilter;
import com.espaciosdeportivos.security.JwtUtils;
import com.espaciosdeportivos.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // =============================================
                        // 🔓 RUTAS PÚBLICAS (Sin autenticación)
                        // =============================================

                        // Autenticación
                        .requestMatchers("/api/auth/**").permitAll()

                        // Documentación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Archivos e imágenes
                        .requestMatchers("/api/imagenes/archivo/**").permitAll()
                        .requestMatchers("/api/archivos/**").permitAll()
                        .requestMatchers("/img/**").permitAll()

                        // Rutas públicas generales
                        .requestMatchers("/api/public/**").permitAll()

                        // Canchas (públicas)
                        .requestMatchers("/api/cancha/area/**").permitAll()
                        .requestMatchers("/api/cancha/porid/**").permitAll()
                        .requestMatchers("/api/cancha/equipamientos/**").permitAll()
                        .requestMatchers("/api/cancha/disciplinas/**").permitAll()

                        // Disciplinas (públicas)
                        .requestMatchers("/api/disciplina/porid/**").permitAll()
                        .requestMatchers("/api/disciplina/activos").permitAll()

                        // Reservas (públicas)
                        .requestMatchers("/api/reservas/**").permitAll()

                        // =============================================
                        // RUTAS CON ROLES ESPECÍFICOS
                        // =============================================

                        // SUPERUSUARIO exclusivo
                        .requestMatchers("/api/super/**").hasRole("SUPERUSUARIO")

                        // SUPERUSUARIO y ADMINISTRADOR
                        .requestMatchers("/api/administradores/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/cancha/*/imagenes")
                        .hasAnyRole("ADMINISTRADOR", "SUPERUSUARIO")
                        .requestMatchers("/api/usuario_control/**").hasAnyRole("ADMINISTRADOR", "SUPERUSUARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/*/eliminar").hasRole("ADMINISTRADOR")

                        // ADMINISTRADOR exclusivo
                        .requestMatchers("/api/supervisa/**").hasRole("ADMINISTRADOR")

                        // =============================================
                        // RUTAS MIXTAS (Múltiples roles)
                        // =============================================

                        .requestMatchers("/api/areasdeportivas/**")
                        .hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")
                        .requestMatchers("/api/clientes/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")
                        .requestMatchers("/api/disciplina/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")
                        .requestMatchers("/api/incluye/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")

                        // =============================================
                        // RUTA POR DEFECTO
                        // =============================================
                        .anyRequest().authenticated());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}