package org.example.usermicroservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.usermicroservice.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Operations related to user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Find by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Find by username")
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        if (userService.findByUsername(username).getUsername() == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("username", "User not found"));
        }
        return ResponseEntity.ok(userService.findByUsername(username));
    }
}
