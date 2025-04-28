package com.seek.users.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.Serializable;
import java.lang.String;
import java.util.Date;

import com.seek.users.domain.model.User;

@Getter
@AllArgsConstructor
public class UserCreatedEvent implements Serializable {

    public UserCreatedEvent() {
    }

    private String name;
    private String email;
    private String role;
    private Date createdAt;

    public void setData(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = new Date();
    }
}