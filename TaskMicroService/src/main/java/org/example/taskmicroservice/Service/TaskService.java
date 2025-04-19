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


    private TaskDTO taskToTaskDTOMapper(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(),
                task.getDescription(), task.getStatus().getStatus(),
                task.getDueDate(), task.getProjectId(),
                task.getAssignedUserId(), task.getCreatedAt(),
                task.getUpdatedAt());
    }



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

    public List<TaskDTO> tasksByProjectId(int projectId) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getProjectId() == projectId)
                .map(this::taskToTaskDTOMapper).toList();
    }

    public String deleteTask(int id) {
        taskRepository.deleteById(id);
        log.info("\n\nTask with id {} deleted \n\n", id);
        return "success";
    }

    public TaskDTO updateTask(int id, UpdateTaskDTO task) {
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
        taskRepository.save(updatedTask);

        if (updatedTask.getAssignedUserId() != null && task.assignedUserId() != oldUsersId) {
            String message = "id: " + task.assignedUserId() + ", title: " + "You have been assigned a task";
            log.info("\n\nSend data from task service to user service" +
                    "(topic = taskTopicUserIdToSendMessage): " + message + "\n\n");
            kafkaProducer.sendRequestToSendNotificationToUser(message);
        }

        return taskToTaskDTOMapper(updatedTask);
    }

    public TaskDTO takeTask(int userId, int taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        TaskStatus status = taskStatusRepository.findByStatus("IN PROGRESS");
        assert task != null;
        task.setAssignedUserId(userId);
        task.setStatus(status);
        task.setUpdatedAt(Timestamp.from(Instant.now()));
        Task updatedTask = taskRepository.save(task);
        return taskToTaskDTOMapper(updatedTask);
    }

    public TaskDTO finishTask(int taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        TaskStatus status = taskStatusRepository.findByStatus("DONE");
        assert task != null;
        task.setStatus(status);
        task.setUpdatedAt(Timestamp.from(Instant.now()));

        // sending message to project's owner that task has been done
        String message = "taskId: " + taskId + ", projectId: " +
                task.getProjectId() + ", userId: " +
                task.getAssignedUserId();

        // send to User service
        kafkaProducer.sendMessageAboutFinishedTask(message);

        Task updatedTask = taskRepository.save(task);

        return taskToTaskDTOMapper(updatedTask);
    }
}
