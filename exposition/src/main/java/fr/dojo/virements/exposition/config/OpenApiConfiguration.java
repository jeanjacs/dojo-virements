package fr.dojo.virements.exposition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {
    @Bean
    OpenAPI transfersOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Transfers API")
                .description("Local banking simulation for internal transfers in euros")
                .version("1.0"));
    }
}
