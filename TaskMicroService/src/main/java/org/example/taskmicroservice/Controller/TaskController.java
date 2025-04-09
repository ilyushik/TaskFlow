package org.example.taskmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.DTO.AddTaskDTO;
import org.example.taskmicroservice.Kafka.KafkaConsumer;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Model.TaskStatus;
import org.example.taskmicroservice.Repository.TaskStatusRepository;
import org.example.taskmicroservice.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final KafkaConsumer kafkaConsumer;
    private final TaskStatusRepository taskStatusRepository;

    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody AddTaskDTO addTaskDTO) throws ExecutionException,
            InterruptedException, TimeoutException {
        int projectId = kafkaConsumer.resolveProjectId(addTaskDTO.getProjectName());
        TaskStatus status = taskStatusRepository.findTaskStatusByStatus("TO DO");
        Task newTask = new Task(addTaskDTO.getTitle(), addTaskDTO.getDescription(), status, addTaskDTO.getPriority(),
                addTaskDTO.getDueDate(), projectId, addTaskDTO.getAssignedUserId() != null ? addTaskDTO.getAssignedUserId() : null);

        return ResponseEntity.ok(taskService.addTask(newTask));
    }
}
