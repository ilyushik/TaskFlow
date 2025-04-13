package org.example.taskmicroservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskDTO {
    private String title;

    private String description;

    private int priority;

    private Date dueDate;

    private Integer assignedUserId;

    private String status;
}

