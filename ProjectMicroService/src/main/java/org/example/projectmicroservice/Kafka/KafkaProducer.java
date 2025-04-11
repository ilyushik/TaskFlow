package org.example.projectmicroservice.Kafka;

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

    public void sendEmail(int owner_id, String title) {
        String message = "owner_id: " + owner_id + ", " + "title: " + title;
        logger.info("\n\nSend message from project service to user service" +
                "(topic = projectTopicSendEmail): " + message + "\n\n");

        // go to user service
        kafkaTemplate.send("projectTopicSendEmail", message);
    }

    public void sendProjectsId(String responseTopic,  String messageResponse) {
        // go to task service
        logger.info("\n\nSend data from project service to task service" +
                "(topic = {}): " + messageResponse + "\n\n", responseTopic);
        kafkaTemplate.send(responseTopic, messageResponse);
    }

    public void sendProjectResultExistProjectWithSuchId(String message) {
        logger.info("\n\nSend data from project service to task service" +
                "(topic = projectTopicResultExistProjectWithSuchId): " + message + "\n\n");
        kafkaTemplate.send("projectTopicResultExistProjectWithSuchId", message);
    }

    // sending username to get id
    public void sendOwnersUsername(String message) {
        logger.info("\n\nSend data from project service to user service" +
                "(topic = projectTopicOwnersId): " + message + "\n\n");
        kafkaTemplate.send("projectTopicOwnersId", message);
    }

    public void sendProjectsDeadline(String message) {
        logger.info("\n\nSend data from project service to user service" +
                "(topic = projectTopicProjectsDeadline): " + message + "\n\n");
        kafkaTemplate.send("projectTopicProjectsDeadline", message);
    }
}
