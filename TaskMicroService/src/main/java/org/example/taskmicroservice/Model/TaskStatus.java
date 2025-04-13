package org.example.taskmicroservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_status")
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "status")
    private List<Task> tasks;

    @Override
    public String toString() {
        return "TaskStatus{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
