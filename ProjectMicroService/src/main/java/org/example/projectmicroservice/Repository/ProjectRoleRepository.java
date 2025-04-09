package org.example.projectmicroservice.Repository;

import org.example.projectmicroservice.Model.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Integer> {
}
