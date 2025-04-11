package org.example.taskmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.DTO.AddTaskDTO;
import org.example.taskmicroservice.Kafka.KafkaConsumer;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Model.TaskStatus;
import org.example.taskmicroservice.Repository.TaskStatusRepository;
import org.example.taskmicroservice.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final KafkaConsumer kafkaConsumer;
    private final TaskStatusRepository taskStatusRepository;

    @GetMapping("/{projectId}")
    public ResponseEntity<?> tasksByProject(@PathVariable("projectId") int id)
            throws ExecutionException,
            InterruptedException, TimeoutException {
        boolean projectExists = kafkaConsumer.projectWithSuchIdExists(id);
        if (!projectExists) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("project",
                    "Project Not Found"));
        }
        return ResponseEntity.ok(taskService.tasksByProjectId(id));
    }

    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody AddTaskDTO addTaskDTO) throws ExecutionException,
            InterruptedException, TimeoutException {
        int projectId = kafkaConsumer.resolveProjectId(addTaskDTO.getProjectName());
        List<Task> tasksByProject = taskService.tasksByProjectId(projectId);
        if (!tasksByProject.stream().filter(t -> t.getTitle()
                .equals(addTaskDTO.getTitle())).toList().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("title",
                    "The task with such title already exists"));
        }
        if (!tasksByProject.stream().filter(t -> t.getDescription()
                .equals(addTaskDTO.getDescription())).toList().isEmpty()) {
            return ResponseEntity
                    .badRequest().body(Collections.singletonMap("description",
                            "The task with such description already exists"));
        }

        if (kafkaConsumer.getProjectsDeadline(addTaskDTO.getProjectName())
                .before(addTaskDTO.getDueDate())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("dueDate",
                    "Deadline of task can not be after project's deadline"));
        }

        TaskStatus status = taskStatusRepository.findTaskStatusByStatus("TO DO");
        Task newTask = new Task(addTaskDTO.getTitle(), addTaskDTO.getDescription(), status,
                addTaskDTO.getPriority(),
                addTaskDTO.getDueDate(), projectId, addTaskDTO.getAssignedUserId() != null ?
                addTaskDTO.getAssignedUserId() : null);

        return ResponseEntity.ok(taskService.addTask(newTask));
    }
}
