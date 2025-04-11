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

    // send user's id
    public void returnUsersId(String message) {
        LOGGER.info("\n\nSend message from user service to project service" +
                "(topic = usersTopicReturnId): " + message + "\n\n");
        kafkaTemplate.send("usersTopicReturnId", message);
    }
}
