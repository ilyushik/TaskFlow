package org.example.projectmicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

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

        logger.info("\n\nrequestId: " + requestId + "\nprojectName: " + projectName + "\nresponse: " + responseTopic + "\n\n");

        Project project = projectRepository.findByName(projectName).orElse(null);
        assert project != null;
        int id = project.getId();

        String messageResponse = "requestId: " + requestId + ", id: " + id;

        kafkaProducer.sendProjectsId(responseTopic, messageResponse);
    }

    @KafkaListener(topics = "taskTopicExistsProjectWithSuchID", groupId = "TaskGroup")
    public void checkExistsProjectWithSuchID(String message) {
        logger.info("\n\nReceived data from task service(topic = taskTopicExistsProjectWithSuchID): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int projectId = Integer.parseInt(data.get("projectId"));
        logger.info("\n\nData after parsing \nrequestId: " + requestId + "\nprojectId: " + projectId + "\n\n");
        Project project = projectRepository.findById(projectId).orElse(null);
        String result = project != null ? "true" : "false";
        String messageResponse = "requestId: " + requestId + ", result: " + result;
        kafkaProducer.sendProjectResultExistProjectWithSuchId(messageResponse);
    }
}
