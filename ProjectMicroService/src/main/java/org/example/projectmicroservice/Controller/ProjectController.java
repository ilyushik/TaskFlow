package org.example.projectmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Kafka.KafkaConsumer;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final KafkaConsumer kafkaConsumer;

    private final ProjectService projectService;

    @GetMapping("")
    public ResponseEntity<List<Project>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @PostMapping("/createProject")
    public ResponseEntity<?> createProject(@RequestBody Project project)
            throws ExecutionException, InterruptedException, TimeoutException {
        if (!projectService.projectsByName(project.getName()).isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("name",
                    "Project with such already exists"));
        }
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String username = authentication.getName();
        int ownersId = kafkaConsumer.getOwnersId(username);

        Project newProject = new Project(project.getName(), project.getDescription(),
                project.getDeadline(), project.getPriority(), ownersId);

        return ResponseEntity.ok(projectService.addProject(newProject));
    }
}
