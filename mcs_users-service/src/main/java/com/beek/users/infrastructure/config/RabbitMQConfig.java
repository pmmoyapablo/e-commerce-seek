package com.vectora.transactionservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "transaction.exchange";
    public static final String QUEUE_NAME = "transaction.queue";
    public static final String ROUTING_KEY = "transactions.routing.key";

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue transactionQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue transactionQueue, TopicExchange transactionExchange) {
        return BindingBuilder
                .bind(transactionQueue)
                .to(transactionExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}