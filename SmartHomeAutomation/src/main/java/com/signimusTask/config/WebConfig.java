package com.signimusTask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	  @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/api/**")  // Allow all API endpoints for CORS
	                .allowedOrigins("http://localhost:8080")  // Frontend origin
	                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allow common methods
	                .allowCredentials(true);  // Allow cookies if using session-based auth
	    }
}
