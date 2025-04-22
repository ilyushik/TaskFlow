package org.example.taskmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.DTO.TaskDTO;
import org.example.taskmicroservice.DTO.UpdateTaskDTO;
import org.example.taskmicroservice.Kafka.KafkaProducer;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Model.TaskStatus;
import org.example.taskmicroservice.Repository.TaskRepository;
import org.example.taskmicroservice.Repository.TaskStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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


    public TaskDTO taskToTaskDTOMapper(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(),
                task.getDescription(), task.getStatus().getStatus(),
                task.getDueDate(), task.getProjectId(),
                task.getAssignedUserId(), task.getCreatedAt(),
                task.getUpdatedAt());
    }



    @Cacheable(value = "task", key = "#id")
    public Task findTaskById(int id) {
        return taskRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = "tasksList", key = "'tasksByProjectId' + #task.projectId")
    @CachePut(value = "task", key = "#result.getId()")
    public Task addTask(Task task) {
        if (task.getAssignedUserId() != null) {
            String message = "id: " + task.getAssignedUserId() + ", title: " + "You have been assigned a task";
            log.info("\n\nSend data from task service to user service" +
                    "(topic = taskTopicUserIdToSendMessage): " + message + "\n\n");
            kafkaProducer.sendRequestToSendNotificationToUser(message);
        }
        return taskRepository.save(task);
    }

    @Cacheable(value = "tasksList", key = "'tasksByProjectId' + #projectId")
    public List<Task> tasksByProjectId(int projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Caching(evict = {
            @CacheEvict(value = "tasksList", key = "'tasksByProjectId' + #task.projectId"),
            @CacheEvict(value = "task", key = "#task.id")
    })
    public String deleteTask(Task task) {
        taskRepository.delete(task);
        return "success";
    }


    @Caching(evict = {
            @CacheEvict(value = "tasksList", key = "'tasksByProjectId' + #task.projectId",
                    condition = "#task.projectId != null"),
            @CacheEvict(value = "task", key = "#id")
    }, put = {
            @CachePut(value = "task", key = "#id")
    })
    public Task updateTask(int id, UpdateTaskDTO task) {
        TaskStatus status = taskStatusRepository.findByStatus(task.status());

        Task updatedTask = taskRepository.findById(id).orElse(null);

        assert updatedTask != null;
        Integer oldUsersId = updatedTask.getAssignedUserId();
        updatedTask.setTitle(task.title());
        updatedTask.setDescription(task.description());
        updatedTask.setStatus(status);
        updatedTask.setPriority(task.priority());
        updatedTask.setDueDate(task.dueDate());
        updatedTask.setAssignedUserId(task.assignedUserId());
        updatedTask.setUpdatedAt(Timestamp.from(Instant.now()));

        log.info("\n\nTask updated: " + updatedTask.toString() + "\n\n");
        Task updatedTask1 = taskRepository.save(updatedTask);

        if (updatedTask.getAssignedUserId() != null && task.assignedUserId() != oldUsersId) {
            String message = "id: " + task.assignedUserId() + ", title: " + "You have been assigned a task";
            log.info("\n\nSend data from task service to user service" +
                    "(topic = taskTopicUserIdToSendMessage): " + message + "\n\n");
            kafkaProducer.sendRequestToSendNotificationToUser(message);
        }

        return updatedTask1;
    }


    @Caching(evict = {
            @CacheEvict(value = "tasksList", key = "'tasksByProjectId' + #task.projectId"),
            @CacheEvict(value = "task", key = "#task.id")
    }, put = {
            @CachePut(value = "task", key = "#task.id")
    })
    public TaskDTO takeTask(int userId, Task task) {
        TaskStatus status = taskStatusRepository.findByStatus("IN PROGRESS");
        task.setAssignedUserId(userId);
        task.setStatus(status);
        task.setUpdatedAt(Timestamp.from(Instant.now()));
        Task updatedTask = taskRepository.save(task);
        return taskToTaskDTOMapper(updatedTask);
    }

    @Caching(evict = {
            @CacheEvict(value = "tasksList", key = "'tasksByProjectId' + #task.projectId"),
            @CacheEvict(value = "task", key = "#task.id")
    }, put = {
            @CachePut(value = "task", key = "#task.id")
    })
    public TaskDTO finishTask(Task task) {
        TaskStatus status = taskStatusRepository.findByStatus("DONE");
        assert task != null;
        task.setStatus(status);
        task.setUpdatedAt(Timestamp.from(Instant.now()));

        // sending message to project's owner that task has been done
        String message = "taskId: " + task.getId() + ", projectId: " +
                task.getProjectId() + ", userId: " +
                task.getAssignedUserId();

        // send to User service
        kafkaProducer.sendMessageAboutFinishedTask(message);

        Task updatedTask = taskRepository.save(task);

        return taskToTaskDTOMapper(updatedTask);
    }
}
