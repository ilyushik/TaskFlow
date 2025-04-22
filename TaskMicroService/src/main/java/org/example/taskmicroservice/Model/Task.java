package org.example.taskmicroservice.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id")
    private TaskStatus status;

    @Column(name = "priority")
    private int priority;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "project_id")
    private int projectId;

    @Column(name = "assigned_user_id")
    private Integer assignedUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Task(String title, String description, TaskStatus status, int priority,
                Date dueDate, int projectId, Integer assignedUserId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
        this.createdAt = Timestamp.from(Instant.now());
    }
}
