package org.example.projectmicroservice.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Repository.ProjectRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ProjectRepository projectRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

//    @KafkaListener(topics = "topicToken", groupId = "ProjectGroup")
//    public void listenToken(String message) {
//        logger.info("Token: " + message);
//    }

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

    @KafkaListener(topics = "taskTopicProjectId", groupId = "ProjectGroup")
    public void handleProjectId(String message) {
        logger.info("Got data from task service: " + message);

        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        String projectName = data.get("projectName");
        String responseTopic = data.get("response");

        logger.info("requestId: " + requestId + "\nprojectName: " + projectName + "\nresponse: " + responseTopic);

        Project project = projectRepository.findByName(projectName).orElse(null);
        assert project != null;
        int id = project.getId();

        String messageResponse = "requestId: " + requestId + ", id: " + id;

        kafkaTemplate.send(responseTopic, messageResponse);
    }
}
