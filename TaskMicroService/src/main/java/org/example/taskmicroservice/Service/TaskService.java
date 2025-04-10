package org.example.taskmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.taskmicroservice.Model.Task;
import org.example.taskmicroservice.Repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public String addTask(Task task) {
        taskRepository.save(task);
        return "success";
    }

    public List<Task> tasksByProjectId(int projectId) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getProjectId() == projectId).toList();
    }
    
}
