package com.unlins.chatbot.config; // Ajuste se o caminho do seu pacote for diferente

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Aqui criamos a ferramenta
    }
}