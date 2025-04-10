package org.example.projectmicroservice.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic projectTopic() {
        return new NewTopic("projectTopic", 1, (short) 1);
    }

    @Bean
    public NewTopic projectTopicProjectId() {
        return new NewTopic("projectTopicProjectId", 1, (short) 1);
    }

    @Bean
    public NewTopic projectTopicSendEmail() {
        return new NewTopic("projectTopicSendEmail", 1, (short) 1);
    }

    @Bean
    public NewTopic projectTopicResultExistProjectWithSuchId() {
        return new NewTopic("projectTopicResultExistProjectWithSuchId", 1, (short) 1);
    }
}
