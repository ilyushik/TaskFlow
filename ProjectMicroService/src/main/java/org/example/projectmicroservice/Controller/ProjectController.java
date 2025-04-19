package org.example.projectmicroservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.DTO.AddProjectDTO;
import org.example.projectmicroservice.Kafka.KafkaConsumer;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
@Tag(name = "Project", description = "Operations related to projects")
public class ProjectController {

    private final KafkaConsumer kafkaConsumer;

    private final ProjectService projectService;

    @Operation(summary = "Get all projects")
    @GetMapping("")
    public ResponseEntity<List<Project>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @Operation(summary = "Create Project")
    @PostMapping("/createProject")
    public ResponseEntity<?> createProject(@RequestBody AddProjectDTO project)
            throws ExecutionException, InterruptedException, TimeoutException {
        if (!projectService.projectsByName(project.name()).isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("name",
                    "Project with such name already exists"));
        }
        if (project.deadline().toInstant().isBefore(Instant.from(Instant.now()))) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("deadline",
                    "Deadline can not be before current date"));
        }
        if (project.priority() > 5 || project.priority() < 1) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("priority",
                    "Priority should be between 1 and 5"));
        }
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String username = authentication.getName();
        int ownersId = kafkaConsumer.getOwnersId(username);

        Project newProject = new Project(project.name(), project.description(),
                project.deadline(), project.priority(), ownersId);

        return ResponseEntity.ok(projectService.addProject(newProject));
    }
}
