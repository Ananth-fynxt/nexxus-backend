package nexxus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @GetMapping("/health")
    public String health() {
        return "NEXXUS-BACKEND is running successfully!";
    }
    
    @GetMapping("/")
    public String home() {
        return "Welcome to NEXXUS-BACKEND - Spring Boot with JDBC";
    }
}
