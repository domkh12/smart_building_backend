package edu.npic.smartBuilding.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Value("${spsapi.openapi.prod-url}")
    private String prodUrl;

    @Value("${spsapi.openapi.dev-url}")
    private String devUrl;

    @Value("${spsapi.openapi.stage-url}")
    private String stageUrl;

    @Bean
    public OpenAPI openAPI() {

        Server localServer = new Server();
        localServer.setUrl(devUrl);
        localServer.setDescription("Server URL in Development environment");

        Server stage = new Server();
        stage.setUrl(stageUrl);
        stage.setDescription("Server URL in Stage environment");

        Server prod = new Server();
        stage.setUrl(prodUrl);
        stage.setDescription("Server URL in Prod environment");
        return new OpenAPI().

                addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Smart parking system API")
                        .description("This is Smart parking system API.")
                        .version("1.0").contact(new Contact()
                                .name("Npic")
                                .email("chanudomei122@gmail.com")
                                .url("https://npic.edu.kh/en/"))

                )
                .servers(List.of(prod,stage,localServer));
    }
}
