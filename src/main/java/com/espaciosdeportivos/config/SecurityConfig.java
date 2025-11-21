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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//agrego esto
import org.springframework.http.HttpMethod;

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

                // Autenticación
                .requestMatchers("/api/auth/**").permitAll()

                // Lectura pública de canchas, áreas, disciplinas (solo GETs)
                .requestMatchers(HttpMethod.GET, "/api/areasdeportivas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cancha/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/disciplina/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cancha/equipamientos/**").permitAll()

                //.requestMatchers("/api/areasdeportivas/**").permitAll()//validar mejor es que yo necesito las ctivas y listar areas deportivas por id eso necsito
                //.requestMatchers("/api/cancha/area/**").permitAll()
                //.requestMatchers("/api/cancha/porid/**").permitAll() //ojito cambie ladirecion de id
                //.requestMatchers("/api/cancha/equipamientos/**").permitAll()
                //.requestMatchers("/api/cancha/disciplinas/**").permitAll()
                //.requestMatchers("/api/cancha/disciplinas/**").permitAll()
                //.requestMatchers("/api/disciplina/porid/**").permitAll()
                //.requestMatchers("/api/disciplina/activos").permitAll()

                // Imágenes y recursos estáticos
                .requestMatchers("/img/**").permitAll() // 
                .requestMatchers("/api/public/**").permitAll()

                // Swagger / OpenAPI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                /* ==================== SUPERUSUARIO (acceso total a rutas /super) ==================== */
                .requestMatchers("/api/super/**").hasRole("SUPERUSUARIO")
                //rutas restringidas por rol
                // Rutas exclusivas para SUPERUSUARIO
                .requestMatchers("/api/areasdeportivas/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE", "USUARIO_CONTROL")
                //agregando al usuario de control para verificar funcionamiento
                //.requestMatchers("/api/**").hasRole("SUPERUSUARIO")
                //.requestMatchers("/api/cancha/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR")   
                //.requestMatchers("/api/admin/**").hasRole("SUPERUSUARIO") ESTO DEJENLO COMENTADO!
                //.requestMatchers("/api/areasdeportivas/**").hasRole("SUPERUSUARIO")


                /* ==================== ADMINISTRADOR y SUPERUSUARIO ==================== */
                .requestMatchers("/api/admin/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR")
                .requestMatchers("/api/administradores/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR")
                //.requestMatchers("/api/areasdeportivas/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR" )
                //.requestMatchers("api/disciplina/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR")
                
                //RUTAS para ADMINISTRADOR
                .requestMatchers("/api/cancha/area/**").hasRole("ADMINISTRADOR") // Solo admins pueden ver canchas por área                .requestMatchers("/api/supervisa/**").hasAnyRole("ADMINISTRADOR") //k
                .requestMatchers("/api/supervisa/**").hasRole("ADMINISTRADOR") //solo administrador puede supervisar sus canchas y usuarios               
                .requestMatchers("/api/usuario_control/**").hasAnyRole("ADMINISTRADOR", "SUPERUSUARIO") //solo admins pueden gestionar sus usuarios de control
                .requestMatchers("/api/incluye/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE") // admins pueden gestionar incluye

                // Rutas que incluyen clientes
                //.requestMatchers("/api/clientes", "/api/clientes/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")
                .requestMatchers("/api/clientes/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")
                .requestMatchers("api/disciplina/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR", "CLIENTE")
                //.requestMatchers("/api/incluye/**").hasAnyRole("SUPERUSUARIO", "ADMINISTRADOR","CLIENTE") // admins pueden gestionar incluye 
                .requestMatchers("/api/reservas/**").hasAnyRole("ADMINISTRADOR", "SUPERUSUARIO","CLIENTE") //admins pueden ver reservas de sus canchas

                // Por defecto, autenticado
                .anyRequest().authenticated()
            );

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
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

