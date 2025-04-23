package org.example.taskmicroservice.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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

    @Autowired
    private ObjectMapper objectMapper;


    private List<Task> parsedList(List<Task> tasks) {
        List<Task> fixedList = new ArrayList<>();

        for (Object obj : tasks) {
            if (obj instanceof Task) {
                fixedList.add((Task) obj);
            } else if (obj instanceof LinkedHashMap) {
                Task task = objectMapper.convertValue(obj, Task.class);
                fixedList.add(task);
            } else {
                // лог на случай неожиданного типа
                System.out.println("Unexpected type: " + obj.getClass());
            }
        }

        return fixedList;
    }


    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Operation(summary = "Find tasks by project")
    @GetMapping("/project/{projectId}")
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

    @Operation(summary = "Find task")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable int id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }


    @Operation(summary = "Add task")
    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody AddTaskDTO addTaskDTO) throws
            ExecutionException, InterruptedException, TimeoutException {
        int projectId = kafkaConsumer.resolveProjectId(addTaskDTO.projectName());

        TaskStatus status = taskStatusRepository.findByStatus("TO DO");

        Task taskFromDTO = new Task(addTaskDTO.title(), addTaskDTO.description(), status,
                addTaskDTO.priority(),
                addTaskDTO.dueDate(), projectId, addTaskDTO.assignedUserId());
        List<Task> tasksByProject = taskService.tasksByProjectId(projectId);

        List<Task> fixedList = parsedList(tasksByProject);


        if (!fixedList.stream().filter(t -> t.getTitle()
                .equals(taskFromDTO.getTitle())).toList().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("title",
                    "The task with such title already exists"));
        }
        if (!fixedList.stream().filter(t -> t.getDescription()
                .equals(taskFromDTO.getDescription())).toList().isEmpty()) {
            return ResponseEntity
                    .badRequest().body(Collections.singletonMap("description",
                            "The task with such description already exists"));
        }

        if (kafkaConsumer.getProjectsDeadline(addTaskDTO.projectName())
                .before(taskFromDTO.getDueDate())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("dueDate",
                    "Deadline of task can not be after project's deadline"));
        }

        Task saved = taskService.addTask(taskFromDTO);
        TaskDTO dto = taskService.taskToTaskDTOMapper(saved);
        return ResponseEntity.ok(dto);

    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/deleteTask/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") int id) {
        Task task = taskService.findTaskById(id);
        if (task == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Task not found"));
        }
        return ResponseEntity.ok(taskService.deleteTask(task));
    }

    @Operation(summary = "Update task")
    @PutMapping("/updateTask/{id}")
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskDTO updateTaskDTO,
                                        @PathVariable("id") int id) throws ExecutionException,
            InterruptedException, TimeoutException {
        Task task = taskService.findTaskById(id);
        int projectId = task.getProjectId();

        TaskStatus status = taskStatusRepository.findByStatus(updateTaskDTO.status());
        Task taskFromDTO = new Task(updateTaskDTO.title(), updateTaskDTO.description(), status,
                updateTaskDTO.priority(),
                updateTaskDTO.dueDate(), projectId, updateTaskDTO.assignedUserId());
        List<Task> tasksByProject = taskService.tasksByProjectId(projectId);

        List<Task> fixedList = parsedList(tasksByProject);

        if (!fixedList.stream().filter(t -> t.getTitle()
                .equals(taskFromDTO.getTitle()) && t.getId() != id).toList().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("title",
                    "The task with such title already exists"));
        }
        if (!fixedList.stream().filter(t -> t.getDescription()
                .equals(taskFromDTO.getDescription()) && t.getId() != id).toList().isEmpty()) {
            return ResponseEntity
                    .badRequest().body(Collections.singletonMap("description",
                            "The task with such description already exists"));
        }

        if (kafkaConsumer.getProjectsDeadlineById(projectId)
                .before(taskFromDTO.getDueDate())) {
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

        Task task = taskService.findTaskById(id);

        return ResponseEntity.ok(taskService.takeTask(userId, task));
    }

    @Operation(summary = "Finish task")
    @GetMapping("/finishTask/{id}")
    public ResponseEntity<?> finishTask(@PathVariable("id") int id) {
        Task task = taskService.findTaskById(id);
        return ResponseEntity.ok(taskService.finishTask(task));
    }
}
