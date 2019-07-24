package io.penguinstats.configuration;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.base.Predicate;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

	@Bean
	public Docket backend() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(apis()).paths(paths()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("Penguin Stats Backend API", "", "1.2.2", "", null, "", "", new ArrayList());
	}

	private Predicate<RequestHandler> apis() {
		return RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class);
	}

	private Predicate<String> paths() {
		return PathSelectors.any();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/META-INF/resources/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/swagger/v2/api-docs", "/v2/api-docs");
		registry.addRedirectViewController("/swagger/swagger-resources/configuration/ui",
				"/swagger-resources/configuration/ui");
		registry.addRedirectViewController("/swagger/swagger-resources/configuration/security",
				"/swagger-resources/configuration/security");
		registry.addRedirectViewController("/swagger/swagger-resources", "/swagger-resources");
	}
}
