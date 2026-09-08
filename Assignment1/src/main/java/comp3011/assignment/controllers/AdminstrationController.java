package comp3011.assignment.controllers;

import java.time.Duration;
import java.time.Instant;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment.Assignment1Application;
import comp3011.assignment.components.schemas.ErrorResponse;
import comp3011.assignment.components.schemas.ShutdownResponse;
import comp3011.assignment.components.schemas.UptimeResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminstrationController {
	
	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
			
    @GetMapping("/uptime")
    public ResponseEntity<?> getUptime() {
    	
    	// Get the server start time and current start time.
        Instant start = Assignment1Application.SERVER_START_TIME;
        Instant now = Instant.now();
        
        // Calculate the uptimeSeconds
        double uptimeSeconds = Duration.between(start, now).getSeconds();
 
        // Return the server start, current start and uptimeSeconds with 200 status
        return ResponseEntity.accepted().body(new UptimeResponse(start.toString(), now.toString(), uptimeSeconds));
        
    }
    
    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdownRequest(HttpServletRequest request) {

    	// Only the first shutdown request succeeds; later requests return 409.
        if (!shuttingDown.compareAndSet(false, true)) {
            var error = new ErrorResponse(
                Instant.now().toString(), 409, "Conflict",
                "Graceful shutdown is already in progress.", request.getRequestURI());
            return ResponseEntity.status(409).body(error);
        }

        // Return the message with 202 status
        return ResponseEntity.accepted().body(new ShutdownResponse("Graceful shutdown requested."));
    }
}
