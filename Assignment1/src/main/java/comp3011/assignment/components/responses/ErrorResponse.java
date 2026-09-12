package comp3011.assignment.components.responses;

public record ErrorResponse(
	    String timestamp,
	    int status,
	    String error,
	    String message,
	    String path
) {}
