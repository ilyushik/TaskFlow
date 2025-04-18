package org.example.notificationmicroservice.Kafka;

import lombok.RequiredArgsConstructor;
import org.example.notificationmicroservice.Mail.MailStructure;
import org.example.notificationmicroservice.Service.MailService;
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
    private final MailService mailService;

    private Map<String, String> parseMessage(String message) {
        String[] parts = message.split(", ");
        Map<String, String> map = new HashMap<>();
        for (String part : parts) {
            String[] keyValue = part.split(": ");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    @KafkaListener(topics = "usersTopicSendEmail", groupId = "UserGroup")
    public void emailRequest(String message) {
        logger.info("\n\nReceived message from user service(topic = usersTopicSendEmail): {} \n\n", message);

        Map<String, String> map = parseMessage(message);
        String email = map.get("email");
        String title = map.get("title");
        int userId = Integer.parseInt(map.get("userId"));
        logger.info("\nEmail: " + email + "\nTitle: " + title + "\nUserID: " + userId + "\n\n");

        // func to send email to user
        MailStructure mailStructure = new MailStructure(title, title);
        mailService.sendMail(email, mailStructure);
        logger.info("\n\nEmail sent successfully\n\n");

        //save template to db
    }

    @KafkaListener(topics = "usersTopicSendEmailAboutFinishedTask", groupId = "UserGroup")
    public void emailRequestAboutFinishedTask(String message) {
        logger.info("\n\nReceived message from user service(topic = usersTopicSendEmailAboutFinishedTask): {} \n\n", message);
        Map<String, String> map = parseMessage(message);
        int taskId = Integer.parseInt(map.get("taskId"));
        String projectName = map.get("projectName");
        String userName = map.get("userName");
        String ownerEmail = map.get("ownerEmail");
        MailStructure mailStructure = new MailStructure("Info about task", userName +
                " has finished the task " + taskId + " from " + projectName);
        mailService.sendMail(ownerEmail, mailStructure);
    }
}
