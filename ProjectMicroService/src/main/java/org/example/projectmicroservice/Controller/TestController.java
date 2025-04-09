package org.example.projectmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Kafka.KafkaProducer;
import org.example.projectmicroservice.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ProjectService projectService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/projects")
    public ResponseEntity<?> findAllProjects() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/emailKafka")
    public ResponseEntity<?> testKafkaMessages(@RequestParam int owner, @RequestParam String title) {
        kafkaProducer.sendEmail(owner, title);

        return ResponseEntity.ok("Success");
    }
}
