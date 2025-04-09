package org.example.projectmicroservice.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project")
public class Project {
    @Valid

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private Date deadline;

    @Column(name = "priority")
    private int priority;

    @Column(name = "owner_id")
    private int ownerId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Timestamp createdAt;

    public Project(String name, String description, Date deadline, int priority,
                   int ownerId, Timestamp createdAt) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.ownerId = ownerId;
        this.createdAt = Timestamp.from(Instant.now());
    }
}
