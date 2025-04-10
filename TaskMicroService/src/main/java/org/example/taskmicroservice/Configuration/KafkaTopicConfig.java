package org.example.taskmicroservice.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic taskTopic() {
        return new NewTopic("taskTopic", 1, (short) 1);
    }

    @Bean
    public NewTopic taskTopicProjectId() {
        return new NewTopic("taskTopicProjectId", 1, (short) 1);
    }

    @Bean
    public NewTopic taskTopicExistsProjectWithSuchID() {
        return new NewTopic("taskTopicExistsProjectWithSuchID", 1, (short) 1);
    }
}
