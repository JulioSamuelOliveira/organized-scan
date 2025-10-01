package br.com.fiap.organized_scan.user;

import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(OAuth2User principal) {
        var login = Optional.ofNullable(principal.getAttributes().get("login"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("Login não fornecido pelo OAuth2User"));

        var user = userRepository.findByEmail(login + "@github.com");
        return user.orElseGet(() -> userRepository.save(new User(principal, login + "@github.com")));
    }
}