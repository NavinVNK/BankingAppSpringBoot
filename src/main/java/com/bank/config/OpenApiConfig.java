package com.bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Banking Support");
        contact.setEmail("support@bank.com");

        Info info = new Info()
                .title("Automated Banking Operations API")
                .version("1.0.0")
                .description("REST API for managing bank accounts, customer details, transactions, and fixed deposits.")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
