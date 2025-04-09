package org.example.taskmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final TaskRepository taskRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public String addTask(Task task) {
        taskRepository.save(task);
        return "success";
    }

    
}
