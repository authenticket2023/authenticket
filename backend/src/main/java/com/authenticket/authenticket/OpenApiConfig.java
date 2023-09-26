package com.authenticket.authenticket;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Authenticket API Documentation")
                        .description("Authenticket API allows you to create, view and manage your events and tickets depending on your role. Authorization is done based on your jwt token. " +
                                "There are 3 different roles: Admin, Event Organiser and User in which your access will be limited based on.")
                        .version("1.0"));
    }
}