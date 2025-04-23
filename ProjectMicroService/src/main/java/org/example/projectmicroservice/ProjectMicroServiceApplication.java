package org.example.projectmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ProjectMicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectMicroServiceApplication.class, args);
    }

}
