package nexxus.controller;

import nexxus.shared.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    private final DatabaseService databaseService;
    
    @Autowired
    public HealthController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    @GetMapping("/health")
    public String health() {
        return "NEXXUS-BACKEND is running successfully!";
    }
    
    @GetMapping("/")
    public String home() {
        return "Welcome to NEXXUS-BACKEND - Spring Boot with JDBC";
    }
    
    @GetMapping("/health/database")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Map<String, Object> response = new HashMap<>();
        
        boolean isConnected = databaseService.testConnection();
        String databaseInfo = databaseService.getDatabaseInfo();
        
        response.put("status", isConnected ? "UP" : "DOWN");
        response.put("database", databaseInfo);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(isConnected ? 200 : 503).body(response);
    }
}
