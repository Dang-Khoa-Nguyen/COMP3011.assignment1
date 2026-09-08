package comp3011.assignment.components.schemas;

public record ErrorResponse(
	    String timestamp,
	    int status,
	    String error,
	    String message,
	    String path
) {}
