package com.unlins.chatbot.services;

import com.unlins.chatbot.entities.User;
import com.unlins.chatbot.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        // Criptografa a senha antes de salvar no banco
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        return userRepository.save(user);
    }

    public User authenticate(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Compara a senha digitada com o Hash do banco
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Senha incorreta!");
        }
        return user;
    }
}