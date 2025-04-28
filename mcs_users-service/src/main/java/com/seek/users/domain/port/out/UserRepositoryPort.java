package com.seek.users.domain.port.out;

import com.seek.users.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);
    User authenticate(String email, String password);
}