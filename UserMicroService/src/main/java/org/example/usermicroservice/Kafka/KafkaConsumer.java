package org.example.usermicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.example.usermicroservice.Model.User;
import org.example.usermicroservice.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final UserRepository userRepository;

    private Map<String, String> parseMessage(String message) {
        Map<String, String> map = new HashMap<>();
        String[] messages = message.split(", ");
        for (String m : messages) {
            String[] split = m.split(": ", 2);
            map.put(split[0].trim(), split[1].trim());
        }

        return map;
    }

    @KafkaListener(topics = "projectTopicSendEmail", groupId = "ProjectGroup")
    public void getOwnersEmail(String message) {
        LOGGER.info("Received message: {}", message);
        Map<String, String> map = parseMessage(message);
        int id = Integer.parseInt(map.get("owner_id"));
        String title = map.get("title");
        LOGGER.info("Id from message: {}", id);
        User user = userRepository.findById(id).orElse(null);
        String email = user.getEmail();

        String newMessage = "email: " + email + ", title: " + title;

        // go to notification service
        kafkaTemplate.send("usersTopicSendEmail", newMessage);
    }
}
