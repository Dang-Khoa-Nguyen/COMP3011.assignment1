package comp3011.assignment.components.responses;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import comp3011.assignment.components.schemas.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

// Catches any unhandled exception and returns the 500 ErrorResponse shape.
@RestControllerAdvice
public class InternalServerError {
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