package com.espaciosdeportivos.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // agrgado img y file:uploads/img/ para ver imagens en el nav
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Deshabilitar el default resource handler KAREN
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);

        // Solo rutas específicas, KAREN
        registry.addResourceHandler("/static/**", "/public/**", "/img/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/", "file:uploads/img/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }
}