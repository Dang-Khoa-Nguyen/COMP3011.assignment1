package comp3011.assignment.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.client.RestClientResponseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TranscriptionService {
	
	private static final Logger log = LoggerFactory.getLogger(TranscriptionService.class);
	private final RestClient client;
    private final String apiKey;
    private final TokenCounterService tokenCounterService;

    /* Injects the key from the OPENAI_API_KEY environment variable at startup.
     * 
     * @param apiKey the api key is injected from environment file 
     * */
    public TranscriptionService(RestClient.Builder builder, TokenCounterService tokenCounterService, @Value("${OPENAI_API_KEY}") String apiKey) {
    	// Ensure the apiKey is not empty.
    	if (apiKey == null || apiKey.isEmpty()) {
    		 throw new IllegalStateException("OPENAI_API_KEY is not set");
    	}
    	this.apiKey = apiKey;
        this.tokenCounterService = tokenCounterService;
        
        this.client = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
    
    /* Transcribe from audio to text. 
     * 
     * @param file the audio file to transcribe
     * @return the transcribed text
     * 
     * @throws IOException if the audio file cannot be read
	 * @throws HttpClientErrorException if the external transcription API returns an error
	 * @throws RuntimeException for unexpected processing errors
     * */
    public String transcribe(MultipartFile file) throws IOException {
    	
    	// OpenAI guesses the format from the extension.
        var audioPart = new ByteArrayResource(file.getBytes()) {
            @Override 
            public String getFilename() { 
            	return "audio.webm"; 
            }
        };
        
        // Create the multipart/form-data for the OpenAI's transcription.
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", audioPart);
        body.add("model", "gpt-4o-mini-transcribe");

        try {
        	// Parse into map get a text field
            var response = client.post()
            	.uri("/audio/transcriptions")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(Map.class);
            
            tokenCounterService.record(response);
            return response.get("text").toString();
        } catch (RestClientResponseException e) {
        	log.error("API call failed with status {}", e.getStatusCode());
            throw e;
        }
    }
}
