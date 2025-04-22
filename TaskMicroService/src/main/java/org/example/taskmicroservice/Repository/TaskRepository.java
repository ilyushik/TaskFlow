package org.example.taskmicroservice.Repository;

import org.example.taskmicroservice.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByProjectId(int projectId);
}
