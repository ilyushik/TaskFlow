package org.example.taskmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.DTO.UpdateTaskDTO;
import org.example.taskmicroservice.Kafka.KafkaProducer;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Model.TaskStatus;
import org.example.taskmicroservice.Repository.TaskRepository;
import org.example.taskmicroservice.Repository.TaskStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskStatusRepository taskStatusRepository;
    private final KafkaProducer kafkaProducer;

    public Task findTaskById(int id) {
        return taskRepository.findById(id).orElse(null);
    }

    public String addTask(Task task) {
        if (task.getAssignedUserId() != null) {
            String message = "id: " + task.getAssignedUserId() + ", title: " + "You have been assigned a task";
            log.info("\n\nSend data from task service to user service" +
                    "(topic = taskTopicUserIdToSendMessage): " + message + "\n\n");
            kafkaProducer.sendRequestToSendNotificationToUser(message);
        }
        taskRepository.save(task);
        return "success";
    }

    public List<Task> tasksByProjectId(int projectId) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getProjectId() == projectId).toList();
    }

    public String deleteTask(int id) {
        taskRepository.deleteById(id);
        log.info("\n\nTask with id {} deleted \n\n", id);
        return "success";
    }

    public Task updateTask(int id, UpdateTaskDTO task) {
        TaskStatus status = taskStatusRepository.findByStatus(task.getStatus());

        Task updatedTask = taskRepository.findById(id).orElse(null);

        assert updatedTask != null;
        Integer oldUsersId = updatedTask.getAssignedUserId();
        updatedTask.setTitle(task.getTitle());
        updatedTask.setDescription(task.getDescription());
        updatedTask.setStatus(status);
        updatedTask.setPriority(task.getPriority());
        updatedTask.setDueDate(task.getDueDate());
        updatedTask.setAssignedUserId(task.getAssignedUserId());
        updatedTask.setUpdatedAt(Timestamp.from(Instant.now()));

        log.info("\n\nTask updated: " + updatedTask.toString() + "\n\n");
        taskRepository.save(updatedTask);

        if (updatedTask.getAssignedUserId() != null && task.getAssignedUserId() != oldUsersId) {
            String message = "id: " + task.getAssignedUserId() + ", title: " + "You have been assigned a task";
            log.info("\n\nSend data from task service to user service" +
                    "(topic = taskTopicUserIdToSendMessage): " + message + "\n\n");
            kafkaProducer.sendRequestToSendNotificationToUser(message);
        }

        return updatedTask;
    }
}
