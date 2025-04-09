package org.example.projectmicroservice.Repository;

import org.example.projectmicroservice.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    public Optional<Project> findByName(String projectName);
}
