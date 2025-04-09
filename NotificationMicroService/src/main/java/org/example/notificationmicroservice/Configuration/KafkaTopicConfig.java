package org.example.notificationmicroservice.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("notificationTopic", 1, (short) 1);
    }
}
