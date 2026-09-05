package com.unlins.chatbot.services;


import com.unlins.chatbot.entities.ChatMessage;
import com.unlins.chatbot.entities.User;
import com.unlins.chatbot.repositories.ChatMessageRepository;
import com.unlins.chatbot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Value ("${gemini.api.url}")
    private String geminiApiUrl;


    @Value ("${gemini.api.key}")
    private String geminiApiKey;


    private final RestTemplate restTemplate;


    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }


    public String messageProcess (String userMessage, Long userId){

        String apiGem = geminiApiUrl + "?key=" + geminiApiKey;

        System.out.println("MENSAGEM REAL QUE CHEGOU NO SERVICE: [" + userMessage + "]");

        String requestAPI = """ 
                {
                "contents": [{
            "parts":[{"text": "%s"}]
        }]}""".formatted(userMessage);

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestAPI, httpHeaders);

        try {

            String response = restTemplate.postForObject(apiGem, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            String textoLimpo = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text").asText();

            User usuarioRemetente = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco de dados!"));

            ChatMessage newMessage = new ChatMessage();
            newMessage.setUserMessage(userMessage);
            newMessage.setPrompt(textoLimpo);
            newMessage.setUsuario(usuarioRemetente);
            chatMessageRepository.save(newMessage);
            return textoLimpo;
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return ("Ocorreu um erro ao chamar a IA" + e.getMessage());
        }

    }
}






