package com.vectora.transactionservice.infrastructure.adapter.out.messaging;

import com.vectora.transactionservice.application.port.out.messaging.TransactionEventPublisher;
import com.vectora.transactionservice.domain.event.TransactionEvent;
import com.vectora.transactionservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQTransactionEventPublisher implements TransactionEventPublisher {

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