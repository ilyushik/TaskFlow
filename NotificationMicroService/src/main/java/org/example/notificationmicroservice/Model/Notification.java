package org.example.notificationmicroservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "id")
    private NotificationType type;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
