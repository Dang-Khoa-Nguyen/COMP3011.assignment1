package comp3011.assignment.components.exceptions;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import comp3011.assignment.components.responses.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

// Catches any unhandled exception from api and returns some error shape below.
@RestControllerAdvice
public class ApiExceptionHandler {
	
	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErrorResponse> handleMissingFile(HttpServletRequest req) {
	    var body = new ErrorResponse(
	        Instant.now().toString(), 
	        400, 
	        "Bad Request",	
	        "A 'file' part is required.", req.getRequestURI());
	    return ResponseEntity.status(400).body(body);
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleTooLarge(HttpServletRequest req) {
	    var body = new ErrorResponse(
	        Instant.now().toString(), 
	        413, 
	        "Payload Too Large",
	        "The uploaded file exceeds the maximum allowed size.", 
	        req.getRequestURI());
	    return ResponseEntity.status(413).body(body);
	}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest req) {
        var body = new ErrorResponse(
            Instant.now().toString(),
            500,
            "Internal Server Error",
            "An unexpected server error occurred.",
            req.getRequestURI());        
        return ResponseEntity.status(500).body(body);
    }
}