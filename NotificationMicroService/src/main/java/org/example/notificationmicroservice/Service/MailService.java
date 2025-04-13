package org.example.notificationmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.notificationmicroservice.Mail.MailStructure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendMail(String mail, MailStructure mailStructure) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setSubject(mailStructure.getSubject());
        message.setText(mailStructure.getMessage());
        message.setTo(mail);

        mailSender.send(message);
    }
}
