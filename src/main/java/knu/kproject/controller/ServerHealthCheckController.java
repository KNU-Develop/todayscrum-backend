package knu.kproject.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerHealthCheckController {
    @GetMapping("/hc")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running");
    }
}
