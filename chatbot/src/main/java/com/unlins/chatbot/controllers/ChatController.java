package com.unlins.chatbot.controllers;


import com.unlins.chatbot.dtos.ChatRequestDTO;
import com.unlins.chatbot.dtos.MessageRequestDTO;
import com.unlins.chatbot.entities.ChatMessage;
import com.unlins.chatbot.entities.User;
import com.unlins.chatbot.repositories.ChatMessageRepository;
import com.unlins.chatbot.repositories.UserRepository;
import com.unlins.chatbot.services.ChatService;
import com.unlins.chatbot.services.GeminiIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;


    @Autowired
    private GeminiIntegrationService geminiIntegrationService;

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatMessage> chat(@RequestBody ChatRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Busca o histórico do usuário antes de chamar a IA
        List<ChatMessage> history = chatMessageRepository.findByUsuarioId(user.getId());

        // Envia o novo método com contexto
        String aiResponse = geminiIntegrationService.askAiWithContext(request.prompt(), history);

        ChatMessage message = new ChatMessage();
        message.setPrompt(request.prompt());
        message.setResponse(aiResponse);
        message.setUsuario(user);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/chat/history/{userId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable Long userId) {

        List<ChatMessage> history = chatMessageRepository.findByUsuarioId(userId);

        if (history.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 se não tiver mensagens
        }

        return ResponseEntity.ok(history);
    }
}
