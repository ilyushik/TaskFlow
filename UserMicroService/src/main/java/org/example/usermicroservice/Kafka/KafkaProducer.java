package org.example.usermicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    public void sendMessageToNotification(String newMessage) {
        // go to notification service
        LOGGER.info("\n\nSend message from user service to notification service" +
                "(topic = usersTopicSendEmail): {} \n\n", newMessage);
        kafkaTemplate.send("usersTopicSendEmail", newMessage);
    }

    // send user's id to task service
    public void sendIdToTask(String message) {
        LOGGER.info("\n\nSend message from user service to task service" +
                "(topic = usersTopicReturnIdToTask): " + message + "\n\n");
        kafkaTemplate.send("usersTopicReturnIdToTask", message);
    }

    // send project's id to project service to get owner's id
    public void sendProjectId(String message) {
        LOGGER.info("\n\nSend message from user service to project service" +
                "(topic = usersTopicGetProjectOwnerId): " + message + "\n\n");
        kafkaTemplate.send("usersTopicGetProjectOwnerId", message);
    }

    // send email about finished task
    public void sendEmailAboutTask(String message) {
        LOGGER.info("\n\nSend message from user service to notification service" +
                "(topic = usersTopicSendEmailAboutFinishedTask): " + message + "\n\n");
        kafkaTemplate.send("usersTopicSendEmailAboutFinishedTask", message);
    }
}
