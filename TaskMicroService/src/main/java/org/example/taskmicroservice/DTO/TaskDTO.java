package org.example.taskmicroservice.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Date;

public record TaskDTO (
        int id, String title, String description, String status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dueDate,
        int projectId, Integer assignedUserId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")Timestamp createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")Timestamp updatedAt) {}
