package com.conecta.config;

import com.conecta.service.FileStorageService;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final FileStorageService fileStorageService;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadRoot = fileStorageService.getRootPath();
		String location = uploadRoot.toUri().toString();
		if (!location.endsWith("/")) {
			location = location + "/";
		}
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(location);
	}
}
