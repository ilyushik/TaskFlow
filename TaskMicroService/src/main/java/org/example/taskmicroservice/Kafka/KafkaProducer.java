package org.example.taskmicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public void sendRequestToGetProjectId(String message) {
        logger.info("\n\nSent data from task service to project service" +
                "(topic = taskTopicProjectId): " + message + "\n\n");
        // go to project service
        kafkaTemplate.send("taskTopicProjectId", message);
    }

    public void sendRequestToGetProjectsDeadline(String message) {
        logger.info("\n\nSend data from task service to project service" +
                "(topic = taskTopicCheckDeadline): " + message + "\n\n");
        kafkaTemplate.send("taskTopicCheckDeadline", message);
    }

    public void sendRequestToGetProjectsDeadlineById(String message) {
        logger.info("\n\nSend data from task service to project service" +
                "(topic = taskTopicCheckDeadlineId): " + message + "\n\n");
        kafkaTemplate.send("taskTopicCheckDeadlineId", message);
    }

    public void sendRequestToSendNotificationToUser(String message) {
        kafkaTemplate.send("taskTopicUserIdToSendMessage", message);
    }

    public void sendRequestToGetUserId(String message) {
        logger.info("\n\nSend data from task service to user service" +
                "(topic = taskTopicGetUserId): " + message + "\n\n");
        kafkaTemplate.send("taskTopicGetUserId", message);
    }

    public void sendMessageAboutFinishedTask(String message) {
        logger.info("\n\nSend data from task service to user service" +
                "(topic = taskTopicFinishedTask): " + message + "\n\n");
        kafkaTemplate.send("taskTopicFinishedTask", message);
    }
}
