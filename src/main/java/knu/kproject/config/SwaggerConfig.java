package knu.kproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI haeder() {
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Token");

        return new OpenAPI().components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement);
    }
    @Bean
    public GroupedOpenApi projectApi(){
        return GroupedOpenApi.builder()
                .group("project")
                .pathsToMatch("/workspace/**")
                .build();
    }
    @Bean
    public GroupedOpenApi workspaceApi(){
        return GroupedOpenApi.builder()
                .group("workspace")
                .pathsToMatch("/")
                .build();
    }
    @Bean
    public GroupedOpenApi userApi(){
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**")
                .build();
    }
}
