package org.example.taskmicroservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.DTO.AddTaskDTO;
import org.example.taskmicroservice.DTO.TaskDTO;
import org.example.taskmicroservice.DTO.UpdateTaskDTO;
import org.example.taskmicroservice.Kafka.KafkaConsumer;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Model.TaskStatus;
import org.example.taskmicroservice.Repository.TaskStatusRepository;
import org.example.taskmicroservice.Service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Operations related to tasks")
public class TaskController {

    private final TaskService taskService;
    private final KafkaConsumer kafkaConsumer;
    private final TaskStatusRepository taskStatusRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Operation(summary = "Find tasks by project")
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

    @Operation(summary = "Add task")
    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody AddTaskDTO addTaskDTO) throws ExecutionException,
            InterruptedException, TimeoutException {
        int projectId = kafkaConsumer.resolveProjectId(addTaskDTO.projectName());
        List<TaskDTO> tasksByProject = taskService.tasksByProjectId(projectId);
        if (!tasksByProject.stream().filter(t -> t.title()
                .equals(addTaskDTO.title())).toList().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("title",
                    "The task with such title already exists"));
        }
        if (!tasksByProject.stream().filter(t -> t.description()
                .equals(addTaskDTO.description())).toList().isEmpty()) {
            return ResponseEntity
                    .badRequest().body(Collections.singletonMap("description",
                            "The task with such description already exists"));
        }

        if (kafkaConsumer.getProjectsDeadline(addTaskDTO.projectName())
                .before(addTaskDTO.dueDate())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("dueDate",
                    "Deadline of task can not be after project's deadline"));
        }

        TaskStatus status = taskStatusRepository.findByStatus("TO DO");
        Task newTask = new Task(addTaskDTO.title(), addTaskDTO.description(), status,
                addTaskDTO.priority(),
                addTaskDTO.dueDate(), projectId, addTaskDTO.assignedUserId() != null ?
                addTaskDTO.assignedUserId() : null);

        return ResponseEntity.ok(taskService.addTask(newTask));
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/deleteTask/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") int id) {
        Task task = taskService.findTaskById(id);
        if (task == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Task not found"));
        }
        return ResponseEntity.ok(taskService.deleteTask(id));
    }

    @Operation(summary = "Update task")
    @PutMapping("/updateTask/{id}")
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskDTO updateTaskDTO,
                                        @PathVariable("id") int id) throws ExecutionException,
            InterruptedException, TimeoutException {
        Task task = taskService.findTaskById(id);
        int projectId = task.getProjectId();
        List<TaskDTO> tasksByProject = taskService.tasksByProjectId(projectId);
        if (!tasksByProject.stream().filter(t -> t.title()
                .equals(updateTaskDTO.title()) && t.id() != id).toList().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("title",
                    "The task with such title already exists"));
        }
        if (!tasksByProject.stream().filter(t -> t.description()
                .equals(updateTaskDTO.description()) && t.id() != id).toList().isEmpty()) {
            return ResponseEntity
                    .badRequest().body(Collections.singletonMap("description",
                            "The task with such description already exists"));
        }

        if (kafkaConsumer.getProjectsDeadlineById(projectId)
                .before(updateTaskDTO.dueDate())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("dueDate",
                    "Deadline of task can not be after project's deadline"));
        }

        return ResponseEntity.ok(taskService.updateTask(id, updateTaskDTO));
    }

    @Operation(summary = "Take task")
    @GetMapping("/takeTask/{id}")
    public ResponseEntity<?> takeTask(@PathVariable("id") int id) throws ExecutionException,
            InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        int userId = kafkaConsumer.getUserId(username);

        return ResponseEntity.ok(taskService.takeTask(userId, id));
    }

    @Operation(summary = "Finish task")
    @GetMapping("/finishTask/{id}")
    public ResponseEntity<?> finishTask(@PathVariable("id") int id) {
        return ResponseEntity.ok(taskService.finishTask(id));
    }
}
