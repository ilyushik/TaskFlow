package org.example.projectmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Model.Project;
import org.example.projectmicroservice.Repository.ProjectRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Cacheable(value = "allProjects", key = "'allProjects'")
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Caching(evict = {
            @CacheEvict(value = "allProjects", key = "'allProjects'")
    }, put = {
            @CachePut(value = "project", key = "'name-' + #project.name")
    })
    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    @Cacheable(value = "project" , key = "'name-' + #name")
    public Project projectByName(String name) {
        Project project = new Project();
        return projectRepository.findByName(name).orElse(project);
    }

    public Project getProjectById(int id) {
        return projectRepository.findById(id).orElse(null);
    }
}
