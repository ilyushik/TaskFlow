package org.example.projectmicroservice.DTO;

import java.util.Date;

public record AddProjectDTO (
        String name,
        String description,
        Date deadline,
        int priority
) {}
