package comp3011.assignment;

import java.time.Instant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Assignment1Application {
	
	// Create UTC timestamp when the server start.
	public static final Instant SERVER_START_TIME = Instant.now();

	public static void main(String[] args) {
		SpringApplication.run(Assignment1Application.class, args);
	}

}
