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

    // check task's and project's deadline
    @Bean
    public NewTopic taskTopicCheckDeadline() {
        return new NewTopic("taskTopicCheckDeadline", 1, (short) 1);
    }

    // check task's and project's deadline by id
    @Bean
    public NewTopic taskTopicCheckDeadlineById() {
        return new NewTopic("taskTopicCheckDeadlineId", 1, (short) 1);
    }

    @Bean NewTopic taskTopicUserIdToSendMessage() {
        return new NewTopic("taskTopicUserIdToSendMessage", 1, (short) 1);
    }

    // topic to get user's id
    @Bean NewTopic taskTopicGetUserId() {
        return new NewTopic("taskTopicGetUserId", 1, (short) 1);
    }

    @Bean NewTopic taskTopicFinishedTask() {
        return new NewTopic("taskTopicFinishedTask", 1, (short) 1);
    }
}
