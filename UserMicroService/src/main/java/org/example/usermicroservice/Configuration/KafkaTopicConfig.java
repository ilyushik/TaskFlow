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

    @Bean
    public NewTopic usersTopicReturnIdToTask() {
        return new NewTopic("usersTopicReturnIdToTask", 1, (short) 1);
    }

    @Bean
    public NewTopic usersTopicGetProjectOwnerId() {
        return new NewTopic("usersTopicGetProjectOwnerId", 1, (short) 1);
    }

    @Bean
    public NewTopic usersTopicSendEmailAboutFinishedTask() {
        return new NewTopic("usersTopicSendEmailAboutFinishedTask", 1, (short) 1);
    }
}
