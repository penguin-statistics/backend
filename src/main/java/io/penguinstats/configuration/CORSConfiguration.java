package io.penguinstats.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.penguinstats.constant.Constant.CustomHeader;

@Configuration
@EnableWebMvc
public class CORSConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "OPTIONS").allowedOrigins("*")
                .allowedHeaders("origin", "content-type", "accept", "authorization", "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials", CustomHeader.X_PENGUIN_VARIANT)
                .exposedHeaders(CustomHeader.X_PENGUIN_SET_PENGUIN_ID, CustomHeader.X_PENGUIN_COMPATIBLE,
                        CustomHeader.X_PENGUIN_UPGRAGE)
                .allowCredentials(true);
    }

}
