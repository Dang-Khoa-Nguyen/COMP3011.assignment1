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

import comp3011.assignment.controllers.StatsHolder;

import org.springframework.web.client.RestClientResponseException;

@Service
public class TranscriptionService {
	private final RestClient client = RestClient.create();
    private final String apiKey;
    private final StatsHolder stats;

    /* Injects the key from the OPENAI_API_KEY environment variable at startup.
     * 
     * @param apiKey the api key is injected from environment file 
     * */
    public TranscriptionService(StatsHolder stats, @Value("${OPENAI_API_KEY}") String apiKey) {
    	// Ensure the apiKey is not empty.
    	if (apiKey == null || apiKey.isEmpty()) {
    		 throw new IllegalStateException("OPENAI_API_KEY is not set");
    	}
    	this.apiKey = apiKey;
        this.stats = stats;
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
        		.uri("https://api.openai.com/v1/audio/transcriptions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(Map.class);
            
            stats.record(response);
            return response.get("text").toString();
        } catch (RestClientResponseException e) {
            // Prints OpenAI's real error to review, for example, bad key, bad model, file too big.
            System.out.println("OpenAI said " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            throw e;
        }
    }
}
