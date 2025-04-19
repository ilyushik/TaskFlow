package org.example.taskmicroservice.DTO;

import java.util.Date;

public record UpdateTaskDTO (
    String title,
    String description,
    int priority,
    Date dueDate,
    Integer assignedUserId,
    String status
) {}

