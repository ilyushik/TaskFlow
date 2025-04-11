package org.example.projectmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> projectsByName(String name) {
        return findAll().stream().filter(p -> p.getName().equals(name)).toList();
    }
}
