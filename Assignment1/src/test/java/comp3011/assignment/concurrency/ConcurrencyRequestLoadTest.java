package comp3011.assignment.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcurrencyRequestLoadTest {

    @LocalServerPort
    // the random port Spring chose
    int port;   
    
    // Using to log the error of this test
	private static final Logger log = LoggerFactory.getLogger(ConcurrencyRequestLoadTest.class);

    @Test
    void handles250ConcurrentRequests() throws InterruptedException {
        int numberOfRequests = 250;                     
        String url = "http://localhost:" + port + "/api/v1/admin/uptime";
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

        for (int i = 0; i < numberOfRequests; i++) {
        	final int requestNumber = i; 
            pool.submit(() -> {
                try {
                    go.await();                                  
                    var response = http.get()
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
        
        pool.shutdown();

        // Check if all 250 requests are successful.
        assertEquals(numberOfRequests, successes.get());
    }
}