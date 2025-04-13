package org.example.taskmicroservice.Repository;

import org.example.taskmicroservice.Model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {
    public TaskStatus findByStatus(String status);
}
