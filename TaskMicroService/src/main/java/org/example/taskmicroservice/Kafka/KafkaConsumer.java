package org.example.taskmicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final KafkaProducer kafkaProducer;

    private final ConcurrentMap<String, CompletableFuture<Integer>> pendingResponses =
            new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompletableFuture<Boolean>>
            pendingResponsesExistsProjectWithId = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompletableFuture<Date>> projectsDeadline =
            new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompletableFuture<Integer>> pendingResponsesUserId =
            new ConcurrentHashMap<>();

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

    public int resolveProjectId(String projectName) throws ExecutionException,
            InterruptedException, TimeoutException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", projectName: " + projectName +
                ", response: projectTopicProjectId";

        CompletableFuture<Integer> future = new CompletableFuture<>();
        pendingResponses.put(requestId, future);

        kafkaProducer.sendRequestToGetProjectId(message);

        return future.get(5, TimeUnit.SECONDS);
    }



    @KafkaListener(topics = "projectTopicProjectId", groupId = "ProjectGroup")
    public void handleProjectId(String message) {

        logger.info("\n\nReceived data from project service(topic = projectTopicProjectId): " +
                message + "\n\n");

        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int id = Integer.parseInt(data.get("id"));
        logger.info("\n\nRequestId: " + requestId + ", id: " + id + "\n\n");

        CompletableFuture<Integer> future = pendingResponses.remove(requestId);

        if (future != null) {
            future.complete(id);
            logger.info("\n\nFuture complete success " + "\n\n");
        }
    }

    public boolean projectWithSuchIdExists(int projectId) throws ExecutionException,
            InterruptedException, TimeoutException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", projectId: " + projectId;

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingResponsesExistsProjectWithId.put(requestId, future);

        kafkaProducer.sendRequestExistsProjectWithSuchId(message);

        return future.get(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "projectTopicResultExistProjectWithSuchId", groupId = "ProjectGroup")
    public void handleRequestExistsProjectWithSuchId(String message) {
        logger.info("\n\nReceived data from project service" +
                "(topic = projectTopicResultExistProjectWithSuchId): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        boolean result = Boolean.parseBoolean(data.get("result"));
        logger.info("\n\nresult: " + result + "\n\n");

        CompletableFuture<Boolean> future = pendingResponsesExistsProjectWithId.get(requestId);
        if (future != null) {
            future.complete(result);
        }
    }

    // function to get project's deadline
    public Date getProjectsDeadline(String projectsName) throws ExecutionException,
            InterruptedException, TimeoutException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", projectsName: " + projectsName;

        CompletableFuture<Date> future = new CompletableFuture<>();
        projectsDeadline.put(requestId, future);

        kafkaProducer.sendRequestToGetProjectsDeadline(message);

        return future.get(5, TimeUnit.SECONDS);
    }

    // concurrent getting deadline
    @KafkaListener(topics = "projectTopicProjectsDeadline", groupId = "ProjectService")
    public void handleProjectsDeadline(String message) throws ParseException {
        logger.info("\n\nReceived data from project service" +
                "(topic = projectTopicProjectsDeadline): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        String deadline = data.get("projectsDeadline");

        String[] deadlineSplit = deadline.split("-");
        logger.info("\n\nParsed deadline: " + deadlineSplit[0] + "-" + deadlineSplit[1] +
                "-" + deadlineSplit[2] + "\n\n");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = formatter.parse(deadline);

        CompletableFuture<Date> future = projectsDeadline.get(requestId);
        if (future != null) {
            future.complete(newDate);
        }
    }

    public Date getProjectsDeadlineById(int id) throws ExecutionException,
            InterruptedException, TimeoutException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", id: " + id;

        CompletableFuture<Date> future = new CompletableFuture<>();
        projectsDeadline.put(requestId, future);

        kafkaProducer.sendRequestToGetProjectsDeadlineById(message);

        return future.get(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "projectTopicProjectsDeadlineById", groupId = "ProjectService")
    public void handleProjectsDeadlineById(String message) throws ParseException {
        logger.info("\n\nReceived data from project service" +
                "(topic = projectTopicProjectsDeadlineById): " + message + "\n\n");
        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        String deadline = data.get("projectsDeadline");

        String[] deadlineSplit = deadline.split("-");
        logger.info("\n\nParsed deadline: " + deadlineSplit[0] + "-" + deadlineSplit[1] +
                "-" + deadlineSplit[2] + "\n\n");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = formatter.parse(deadline);

        CompletableFuture<Date> future = projectsDeadline.get(requestId);
        if (future != null) {
            future.complete(newDate);
        }
    }

    public int getUserId(String userName) throws ExecutionException, InterruptedException {
        String requestId = UUID.randomUUID().toString();
        String message = "requestId: " + requestId + ", userName: " + userName;

        CompletableFuture<Integer> future = new CompletableFuture<>();
        pendingResponsesUserId.put(requestId, future);

        kafkaProducer.sendRequestToGetUserId(message);

        return future.get();
    }

    @KafkaListener(topics = "usersTopicReturnIdToTask", groupId = "UserGroup")
    public void handleUsersReturnIdToTask(String message) throws ParseException {
        logger.info("\n\nReceived data from UserService" + message + "\n\n");

        Map<String, String> data = parseMessage(message);
        String requestId = data.get("requestId");
        int id = Integer.parseInt(data.get("id"));

        CompletableFuture<Integer> future = pendingResponsesUserId.get(requestId);
        if (future != null) {
            future.complete(id);
        }
    }
}
