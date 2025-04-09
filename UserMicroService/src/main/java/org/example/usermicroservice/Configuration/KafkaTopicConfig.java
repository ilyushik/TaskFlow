package org.example.usermicroservice.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic usersTopicSendEmail() {
        return new NewTopic("usersTopicSendEmail", 1, (short) 1);
    }
}
