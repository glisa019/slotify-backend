package com.myslotify.slotify.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Slotify API",
                version = "v1",
                description = "API documentation for Slotify"
        )
)
public class OpenApiConfig {
}
