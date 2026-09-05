package com.unlins.chatbot.services;

import com.unlins.chatbot.entities.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeminiIntegrationService {

    // Puxa a URL base configurada no properties
    @Value("${gemini.api.url}")
    private String apiUrl;

    // Puxa a chave configurada no properties
    @Value("${gemini.api.key}")
    private String apiKey;

    public String askAiWithContext(String prompt, List<ChatMessage> history) {
        // Monta o contexto juntando as mensagens anteriores
        StringBuilder context = new StringBuilder("Histórico da conversa:\n");
        for (ChatMessage msg : history) {
            context.append("Usuário: ").append(msg.getPrompt()).append("\n");
            if (msg.getResponse() != null) {
                context.append("IA: ").append(msg.getResponse()).append("\n");
            }
        }
        context.append("\nResponda a esta nova interação com base no histórico acima: ").append(prompt);

        String url = apiUrl + "?key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        // Protege contra quebras de linha que invalidam o JSON
        String safePrompt = context.toString().replace("\"", "\\\"").replace("\n", "\\n");
        String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + safePrompt + "\" }] }] }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Erro ao contatar o Gemini: " + e.getMessage();
        }
    }
}