package io.penguinstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("io.penguinstats")
@EnableScheduling
public class PenguinStatisticsApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PenguinStatisticsApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PenguinStatisticsApplication.class, args);
		System.out.println("PenguinStats is running.");
	}

}
