package org.example.usermicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.example.usermicroservice.Model.User;
import org.example.usermicroservice.Repository.UserRepository;
import org.example.usermicroservice.Service.UserService;
import org.example.usermicroservice.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final UserRepository userRepository;

    private final ConcurrentHashMap<String, CompletableFuture<String>> projectOwnerId = new ConcurrentHashMap<>();

    private final KafkaProducer kafkaProducer;

    private final UserService userService;

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
        UserDTO user = userService.findById(id);
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

        UserDTO user = userService.findByUsername(username);
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

        UserDTO user = userService.findById(id);

        assert user != null;
        String newMessage = "email: " + user.getEmail() + ", title: " + title + ", userId: " + id;
        kafkaProducer.sendMessageToNotification(newMessage);
    }

    @KafkaListener(topics = "taskTopicGetUserId", groupId = "TaskGroup")
    public void getUserIdToTakeTask(String message) {
        Map<String, String> map = parseMessage(message);
        String requestId = map.get("requestId");
        String userName = map.get("userName");

        UserDTO user = userService.findByUsername(userName);
        assert user != null;
        int id = user.getId();

        String returnMessage = "requestId: " + requestId + ", id: " + id;
        kafkaProducer.sendIdToTask(returnMessage);
    }

    @KafkaListener(topics = "taskTopicFinishedTask", groupId = "TaskGroup")
    public void getFinishedTask(String message) throws ExecutionException, InterruptedException {
        LOGGER.info("\n\nReceived message from task service(topic = taskTopicFinishedTask): {} \n\n", message);
        Map<String, String> map = parseMessage(message);
        int taskId = Integer.parseInt(map.get("taskId"));
        int projectId = Integer.parseInt(map.get("projectId"));
        int userId = Integer.parseInt(map.get("userId"));
        UserDTO user = userService.findById(userId);

        LOGGER.info("\n\nSend id to project service: {} \n\n", taskId);
        String projectOwnerAndName = ownersId(projectId);
        LOGGER.info("\n\nReceived: {} \n\n", projectOwnerAndName);

        Map<String, String> projectNameOwner = parseMessage(projectOwnerAndName);
        int ownerId = Integer.parseInt(projectNameOwner.get("ownerId"));
        String projectName = projectNameOwner.get("projectName");

        UserDTO userOwner = userService.findById(ownerId);

        assert user != null;
        assert userOwner != null;
        String newMessage = "taskId: " + taskId + ", projectName: " + projectName + ", userName: " + user.getUsername() +
                ", ownerEmail: " + userOwner.getEmail();

        kafkaProducer.sendEmailAboutTask(newMessage);
    }

    public String ownersId(int projectId) throws ExecutionException, InterruptedException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", projectId: " + projectId;

        CompletableFuture<String> future = new CompletableFuture<>();
        projectOwnerId.put(requestId, future);

        kafkaProducer.sendProjectId(message);

        return future.get();
    }

    @KafkaListener(topics = "projectTopicReturnOwnerId", groupId = "ProjectGroup")
    public void ownerIdHandler(String message) {
        LOGGER.info("\n\nReceived message from project service(topic = projectTopicReturnOwnerId): {} \n\n", message);
        Map<String, String> map = parseMessage(message);
        String requestId = map.get("requestId");
        int ownerId = Integer.parseInt(map.get("ownerId"));
        String projectName = map.get("projectName");

        String projectNameOwner = "ownerId: " + ownerId + ", projectName: " + projectName;

        CompletableFuture<String> future = projectOwnerId.get(requestId);
        if (future != null) {
            future.complete(projectNameOwner);
        }
    }
}
