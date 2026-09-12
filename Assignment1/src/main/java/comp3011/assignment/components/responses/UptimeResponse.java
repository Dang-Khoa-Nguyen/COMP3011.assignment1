package comp3011.assignment.components.responses;

public record UptimeResponse(String utcServerStart, String utcNow, double serverUptimeSeconds) 
{}
