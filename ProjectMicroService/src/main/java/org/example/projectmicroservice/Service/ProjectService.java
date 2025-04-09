package org.example.projectmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }
}
