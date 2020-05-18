package io.penguinstats.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author AlvISsReimu
 */
@Configuration
@EnableWebMvc
public class CORSConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("GET", "POST").allowedOrigins("*")
				.allowedHeaders("origin", "content-type", "accept", "authorization", "Access-Control-Allow-Origin",
						"Access-Control-Allow-Credentials")
				.allowCredentials(true);
	}

}
