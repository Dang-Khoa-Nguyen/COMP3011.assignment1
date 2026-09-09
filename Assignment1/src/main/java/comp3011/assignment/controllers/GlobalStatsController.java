package comp3011.assignment.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment.components.schemas.GlobalStatsResponse;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatsController {

	private final StatsHolder stats;
    public GlobalStatsController(StatsHolder stats) { this.stats = stats; }

    @GetMapping("/stats")
    public GlobalStatsResponse getGlobalStats() {
        return new GlobalStatsResponse(stats.input(), stats.output());
    }
    
}
