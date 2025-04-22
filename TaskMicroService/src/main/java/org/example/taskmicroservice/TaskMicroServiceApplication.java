package org.example.taskmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TaskMicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMicroServiceApplication.class, args);
    }

}
