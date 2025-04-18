package org.example.authmicroservice.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic authTopicToken() {
        return new NewTopic("authTopicToken", 1, (short) 1);
    }
}
