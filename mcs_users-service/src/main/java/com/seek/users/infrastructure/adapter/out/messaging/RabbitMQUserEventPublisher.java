package com.seek.users.infrastructure.adapter.out.messaging;

import com.seek.users.application.port.out.messaging.UserEventPublisher;
import com.seek.users.domain.event.UserCreatedEvent;
import com.seek.users.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQUserEventPublisher implements UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter = new Jackson2JsonMessageConverter();

    @Override
    public void publish(TransactionEvent event) {
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

        Message message = messageConverter.toMessage(event, properties);
        rabbitTemplate.send(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
    }
}