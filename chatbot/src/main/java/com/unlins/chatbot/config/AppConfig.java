package com.unlins.chatbot.config; // Ajuste se o caminho do seu pacote for diferente

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// O @Configuration avisa o Spring: "Leia esta classe logo no início, ela tem ferramentas para a sua caixa!"
@Configuration
public class AppConfig {

    // O @Bean avisa: "Pegue o resultado deste método e deixe disponível para quem pedir!"
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Aqui criamos a ferramenta
    }
}