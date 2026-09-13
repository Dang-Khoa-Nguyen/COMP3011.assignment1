package comp3011.assignment.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment.components.responses.GlobalStatsResponse;
import comp3011.assignment.services.TokenCounterService;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatsController {

	private final TokenCounterService tokenCounterService;
	
	public GlobalStatsController(TokenCounterService tokenCounterService) {
        this.tokenCounterService = tokenCounterService;
    }
	
    @GetMapping("/stats")
    public ResponseEntity<GlobalStatsResponse> getGlobalStats() {
        return ResponseEntity.status(200).body(tokenCounterService.getStats());
    }
    
}
