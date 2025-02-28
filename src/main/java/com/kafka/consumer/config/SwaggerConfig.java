package com.kafka.consumer.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "IRCTC Mail Dashboard",
        version = "1.0",
        description = "IRCTC Mail Management System"
))
public class SwaggerConfig {

}
