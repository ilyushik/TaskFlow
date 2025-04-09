package org.example.projectmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmicroservice.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ProjectService projectService;

    @GetMapping("")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/projects")
    public ResponseEntity<?> findAllProjects() {
        return ResponseEntity.ok(projectService.findAll());
    }
}
