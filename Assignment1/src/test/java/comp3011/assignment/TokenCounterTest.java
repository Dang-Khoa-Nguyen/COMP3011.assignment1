package comp3011.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

import comp3011.assignment.services.TokenCounterService;


public class TokenCounterTest {
	
	/**
	 * Race-condition test for TokenCounterService.
	 * 
	 * 200 threads record tokens at the same instant, and the totals must be exact. 
	 * If it weren't thread-safe, concurrent updates would be lost and the counts would come out too low.
	 */
	@Test
    void concurrentUpdatesCountCorrectly() throws InterruptedException {
		TokenCounterService tokenCounterService = new TokenCounterService();
		int numberOfThreads = 200;
		int inputTokens = 20;
		int outputTokens = 10;
		
		 // A fake OpenAI response with 20 input and 10 output tokens
        Map<String, Object> fakeResponse = Map.of("usage", Map.of("input_tokens", inputTokens, "output_tokens", outputTokens));
        
        // CountDownLatch makes all threads start at the same instant.
        // pool runs all tasks concurrently
        // ready counts down as each thread starts, so the main thread knows all are waiting
        // go is a starting gun, which held until every thread is ready and release once.
        // done counts down as each thread finishes, so the main thread knows all are finished.
		ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch ready = new CountDownLatch(numberOfThreads);
		CountDownLatch go = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(numberOfThreads);
		
		for (int thread=0; thread < numberOfThreads; thread++) {
			pool.submit(() -> {
				// To signal that a thread is ready and waiting.
				ready.countDown();
				try {
					// Block until the main thread give signal to run the record together.
					go.await();
					tokenCounterService.record(fakeResponse);
					
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					// To signal that a thread is finished.
					done.countDown();
				}
			});
		}
		
		// Wait until all threads are ready.
		ready.await();
		
		// Release every thread at the same moment
		go.countDown();
		
		// Wait until all threads finished.
		done.await();
		pool.shutdown();
		
		// Check if the TokenCounterService provide correct input token and output token
		// to ensure it is thread-safe 
		assertEquals(numberOfThreads * inputTokens, tokenCounterService.getInput());
		assertEquals(numberOfThreads * outputTokens, tokenCounterService.getOutput());
	}
}
