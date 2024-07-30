package knu.kproject.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;


@Configuration
public class SwaggerConfig {
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
                .pathsToMatch("/UserInfo/**")
                .build();
    }
}
