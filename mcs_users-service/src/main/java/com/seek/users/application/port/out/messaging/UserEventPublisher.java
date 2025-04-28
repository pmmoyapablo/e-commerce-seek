package com.seek.users.application.port.out.messaging;

import com.seek.users.domain.event.UserCreatedEvent;

public interface UserEventPublisher {
    void publish(UserCreatedEvent event);
}