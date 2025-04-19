package org.example.taskmicroservice.DTO;

import java.util.Date;

public record AddTaskDTO (
    String title,
    String description,
    int priority,
    Date dueDate,
    String projectName,
    Integer assignedUserId
) {}
