package org.example.usermicroservice.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@OpenAPIDefinition(info = @Info(title = "My API", version = "v1", description = "My API Description"))
public class OpenApiConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info().title("TaskFlow Authentication User Service"))
                .addSecurityItem(new SecurityRequirement().addList("TaskFlowUserServiceSecurityScheme"))
                .components(new Components().addSecuritySchemes("TaskFlowUserServiceSecurityScheme", new SecurityScheme()
                        .name("TaskFlowUserServiceSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

    }
}
