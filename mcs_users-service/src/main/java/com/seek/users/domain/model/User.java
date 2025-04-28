package com.seek.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data // Lombok: getters, setters, toString, equals, hashCode
@AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private String role;
    

    public User(String name, String email, String password, String role) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;

        validateEmail(email);
        encryptPassword(password);
    }

    private void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @");
        }
    }

    private void encryptPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }
}