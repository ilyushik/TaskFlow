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

    /* send owner_id to user service and then user service sends owner's email to
    notification service */

    public void sendEmail(int owner_id, String title) {
        String message = "owner_id: " + owner_id + ", " + "title: " + title;
        logger.info("Send message: " + message);

        // go to user service
        kafkaTemplate.send("projectTopicSendEmail", message);
    }
}
