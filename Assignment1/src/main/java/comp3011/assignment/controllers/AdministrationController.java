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
import comp3011.assignment.components.ApplicationTerminator;
import comp3011.assignment.components.responses.ErrorResponse;
import comp3011.assignment.components.responses.ShutdownResponse;
import comp3011.assignment.components.responses.UptimeResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin")
public class AdministrationController {
	
	private final ApplicationTerminator terminator;
	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
	
	public AdministrationController(ApplicationTerminator terminator) {
        this.terminator = terminator;
    }
	
    @GetMapping("/uptime")
    public UptimeResponse getServerUptime() {
    	
    	// Get the server start time and current start time.
        Instant start = Assignment1Application.SERVER_START_TIME;
        Instant now = Instant.now();
        
        // Calculate the uptimeSeconds and keep the fractional seconds.
        double uptimeSeconds = Duration.between(start, now).toMillis() / 1000.0;
 
        // Return the server start, current start and uptimeSeconds with 200 status
        return new UptimeResponse(start.toString(), now.toString(), uptimeSeconds);   
    }
    
    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdownServer(HttpServletRequest request) {

    	// Only the first shutdown request succeeds; later requests return 409.
        if (!shuttingDown.compareAndSet(false, true)) {
            var error = new ErrorResponse(
                Instant.now().toString(), 409, "Conflict",
                "Graceful shutdown is already in progress.", request.getRequestURI());
            return ResponseEntity.status(409).body(error);
        }
        
        terminator.terminate();
        
        // Return the message with 202 status
        return ResponseEntity.accepted().body(new ShutdownResponse("Graceful shutdown requested."));
    }
}
