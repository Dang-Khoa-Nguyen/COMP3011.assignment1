package comp3011.assignment.controllers;

import java.time.Duration;
import java.time.Instant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment.Assignment1Application;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminstrationController {
	
    @GetMapping("/uptime")
    public Map<String, Object> getUptime() {
    	
    	// Get the server start time and current start time.
        Instant start = Assignment1Application.SERVER_START_TIME;
        Instant now = Instant.now();
        
        // Calculate the uptime
        long uptimeSeconds = Duration.between(start, now).getSeconds();
        
        // Add the server start time, current start time and uptime seconds in response.
        Map<String, Object> response = new HashMap<>();
        response.put("serverStartUtc", start.toString());
        response.put("currentUtc", now.toString());
        response.put("uptimeSeconds", uptimeSeconds);
        
        System.out.print(response);
        return response;
        
    }
}
