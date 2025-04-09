package org.example.taskmicroservice.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private final ConcurrentMap<String, CompletableFuture<Integer>> pendingResponses = new ConcurrentHashMap<>();

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

    public int resolveProjectId(String projectName) throws ExecutionException, InterruptedException, TimeoutException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", projectName: " + projectName + ", response: projectTopicProjectId";
        logger.info("Sent data from task service: " + message);

        CompletableFuture<Integer> future = new CompletableFuture<>();
        pendingResponses.put(requestId, future);

        kafkaTemplate.send("taskTopicProjectId", message);

        return future.get(5, TimeUnit.SECONDS);
    }



    @KafkaListener(topics = "projectTopicProjectId", groupId = "TaskGroup")
    public void handleProjectId(String message) {

        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int id = Integer.parseInt(data.get("id"));

        CompletableFuture<Integer> future = pendingResponses.remove(requestId);

        if (future != null) {
            future.complete(id);
        }

    }
}
