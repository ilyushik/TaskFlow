package org.example.usermicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.example.usermicroservice.Model.User;
import org.example.usermicroservice.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final UserRepository userRepository;

    private final KafkaProducer kafkaProducer;

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
        LOGGER.info("\n\nReceived message from project service(topic = projectTopicSendEmail): {} \n\n", message);
        Map<String, String> map = parseMessage(message);
        int id = Integer.parseInt(map.get("owner_id"));
        String title = map.get("title");
        LOGGER.info("\n\nId from message: {} \n\n", id);
        User user = userRepository.findById(id).orElse(null);
        String email = user.getEmail();

        String newMessage = "email: " + email + ", title: " + title;

        kafkaProducer.sendMessageToNotification(newMessage);
    }

    @KafkaListener(topics = "projectTopicOwnersId", groupId = "ProjectGroup")
    public void getUsersUsernameAndReturningId(String message) {
        LOGGER.info("\n\nReceived message from project service(topic = projectTopicOwnersId): {} \n\n", message);
        Map<String, String> map = parseMessage(message);
        String requestId = map.get("requestId");
        String username = map.get("username");

        User user = userRepository.findUserByUsername(username).orElse(null);
        assert user != null;
        int id = user.getId();

        String returnMessage = "requestId: " + requestId + ", id: " + id;
        kafkaProducer.returnUsersId(returnMessage);
    }

    @KafkaListener(topics = "taskTopicUserIdToSendMessage", groupId = "TaskGroup")
    public void getUserIdToSendNotification(String message) {
        LOGGER.info("\n\nReceived data from task service(topic = taskTopicUserIdToSendMessage): {} \n\n", message);
        Map<String, String> map = parseMessage(message);
        int id = Integer.parseInt(map.get("id"));
        String title = map.get("title");

        User user = userRepository.findById(id).orElse(null);

        assert user != null;
        String newMessage = "email: " + user.getEmail() + ", title: " + title + ", userId: " + id;
        kafkaProducer.sendMessageToNotification(newMessage);
    }
}
