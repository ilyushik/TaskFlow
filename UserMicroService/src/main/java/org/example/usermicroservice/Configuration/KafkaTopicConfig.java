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

    // getting user's username and returning id
    @Bean
    public NewTopic usersTopicReturnId() {
        return new NewTopic("usersTopicReturnId", 1, (short) 1);
    }
}
