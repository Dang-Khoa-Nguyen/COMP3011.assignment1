package comp3011.assignment.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import comp3011.assignment.services.TranscriptionService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class ConcurrencyRequestLoadTest {

    @LocalServerPort
    // the random port Spring chose
    int port;   
    
    @MockitoBean 
    TranscriptionService transcriptionService;
    
    // Using to log the error of this test
	private static final Logger log = LoggerFactory.getLogger(ConcurrencyRequestLoadTest.class);

    @Test
    void handles250ConcurrentRequests() throws Exception {
    	 // Every transcribe call waits 500ms, imitating a real network call.
        when(transcriptionService.transcribe(any())).thenAnswer(_ -> {
            Thread.sleep(500);
            return "stubbed transcription";
        });

        
        int numberOfRequests = 250;                     
        String url = "http://localhost:" + port + "/api/v1/transcribe";
        RestClient http = RestClient.builder()
        		.baseUrl(url)
        		.build();
        
        // CountDownLatch makes all threads start at the same instant.
        // pool runs all tasks concurrently
        // go is a starting gun, which held until every thread is ready and release once.
        // done counts down the threads that is finished.
        // success counts total of the requests that are successful.
        ExecutorService pool = Executors.newFixedThreadPool(numberOfRequests);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(numberOfRequests);
        AtomicInteger successes = new AtomicInteger(0);
        
        // Start the clock just before firing the requests.
        long start = System.currentTimeMillis();  

        for (int i = 0; i < numberOfRequests; i++) {
        	final int requestNumber = i; 
            pool.submit(() -> {
                try {
                    go.await();        
                    
                    // Create a dummy audio
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", new ByteArrayResource("fake audio".getBytes()) {
                        @Override 
                        public String getFilename() { 
                        	return "audio.webm"; 
                        }
                    });
                    
                    
                    var response = http.post().uri(url)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(body)
                            .retrieve()
                            .toBodilessEntity();   
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                    	successes.incrementAndGet();
                    }
                } catch (Exception e) {
                    // a failure means the count won't reach 250 
                	log.error("Fail on the request: {}", requestNumber);
                } finally {
                    done.countDown();
                }
            });
        }
        
        // release all 250 at once
        go.countDown();   
        
        // wait for all to finish
        done.await();  
        
        // Stop the clock once every request has finished.
        long elapsed = System.currentTimeMillis() - start;
        pool.shutdown();

        // Check if all 250 requests are successful.
        assertEquals(numberOfRequests, successes.get());
        
	     // 250 requests that each block 500ms would take 125s if handled one at a time.
	     // Finishing under 10s proves that the threads ran in parallel
        assertTrue(elapsed < 10_000, "expected concurrent, took " + elapsed + "ms");
    }
}