package org.example.projectmicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ProjectRepository projectRepository;

    private final KafkaProducer kafkaProducer;

    private Map<String, String> parseMessage(String message) {
        Map<String, String> result = new HashMap<>();
        String[] parts = message.split(", ");
        for (String part : parts) {
            String[] keyValue = part.split(": ", 2);
            if (keyValue.length == 2) {
                result.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return result;
    }

    @KafkaListener(topics = "taskTopicProjectId", groupId = "TaskGroup")
    public void handleProjectId(String message) {
        logger.info("\n\nReceived data from task service(topic = taskTopicProjectId): " + message + "\n\n");

        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        String projectName = data.get("projectName");
        String responseTopic = data.get("response");

        logger.info("\n\nrequestId: " + requestId + "\nprojectName: " + projectName +
                "\nresponse: " + responseTopic + "\n\n");

        Project project = projectRepository.findByName(projectName).orElse(null);
        assert project != null;
        int id = project.getId();

        String messageResponse = "requestId: " + requestId + ", id: " + id;

        kafkaProducer.sendProjectsId(responseTopic, messageResponse);
    }

    @KafkaListener(topics = "taskTopicExistsProjectWithSuchID", groupId = "TaskGroup")
    public void checkExistsProjectWithSuchID(String message) {
        logger.info("\n\nReceived data from task service" +
                "(topic = taskTopicExistsProjectWithSuchID): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int projectId = Integer.parseInt(data.get("projectId"));
        logger.info("\n\nData after parsing \nrequestId: " + requestId + "\nprojectId: " +
                projectId + "\n\n");
        Project project = projectRepository.findById(projectId).orElse(null);
        String result = project != null ? "true" : "false";
        String messageResponse = "requestId: " + requestId + ", result: " + result;
        kafkaProducer.sendProjectResultExistProjectWithSuchId(messageResponse);
    }

    public int getOwnersId(String username) throws ExecutionException,
            InterruptedException, TimeoutException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyMSIsInJvbGUiOiJST0xFX1VTRVIiLCJpYXQiOjE3NTY1MDA1NDEsImV4cCI6MTc1NjU4Njk0MX0.nb7X0ALpS0Li6vGg96PlbRl4YBWVDNg3i9DnS-eToy8");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8084/api/v1/user/username/" + username + "/id";
        ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, entity, Integer.class);
        return response.getBody();
    }

    @KafkaListener(topics = "taskTopicCheckDeadline", groupId = "TaskGroup")
    public void handleTasksReturnDeadline(String message) {
        logger.info("\n\nReceived data from task service" +
                "(topic = taskTopicCheckDeadline): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        String projectsName = data.get("projectsName");

        Project project = projectRepository.findByName(projectsName).orElse(null);
        assert project != null;
        String newMessage = "requestId: " + requestId + ", projectsDeadline: " + project.getDeadline();

        kafkaProducer.sendProjectsDeadline(newMessage);
    }

    @KafkaListener(topics = "taskTopicCheckDeadlineId", groupId = "TaskGroup")
    public void handleTasksReturnDeadlineById(String message) {
        logger.info("\n\nReceived data from task service" +
                "(topic = taskTopicCheckDeadlineId): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int id = Integer.parseInt(data.get("id"));

        Project project = projectRepository.findById(id).orElse(null);
        assert project != null;
        String newMessage = "requestId: " + requestId + ", projectsDeadline: " + project.getDeadline();

        kafkaProducer.sendProjectsDeadlineById(newMessage);
    }

    @KafkaListener(topics = "usersTopicGetProjectOwnerId", groupId = "UserGroup")
    public void returnProjectOwner(String message) {
        logger.info("\n\nReceived from user service(topic = usersTopicGetProjectOwnerId): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int id = Integer.parseInt(data.get("projectId"));

        Project project = projectRepository.findById(id).orElse(null);
        assert project != null;
        int ownerId = project.getOwnerId();

        String newMessage = "requestId: " + requestId + ", ownerId: " + ownerId + ", projectName: "  + project.getName();
        kafkaProducer.sendOwnerId(newMessage);
    }
}
