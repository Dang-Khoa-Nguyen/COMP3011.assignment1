package comp3011.assignment.services;


import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.stereotype.Service;

import comp3011.assignment.components.responses.GlobalStatsResponse;

@Service
public class TokenCounterService {
	
	// LongAdder handles concurrent updates safely under heavy load.
    private final LongAdder inputTokens = new LongAdder();
    private final LongAdder outputTokens = new LongAdder();

    /*
     * Adds the token counts from a transcription response.
     */
    public void record(Map<?, ?> response) {
        Object usage = response.get("usage");
        if (usage instanceof Map<?, ?> OpenAITokenUsage) {
            inputTokens.add(toLong(OpenAITokenUsage.get("input_tokens"))); 
            outputTokens.add(toLong(OpenAITokenUsage.get("output_tokens")));
        }
    }
    
    /*
     * Converts a Number to long, or returns 0 if n is instance of Number.
     */
    private long toLong(Object n) {
    	long num = 0;
    	if (n instanceof Number) {
    		num = ((Number) n).longValue();
    	}
    	return num;
        
    }
    
    /*
     * Get the input token's total and output token's total by GlobalStatsResponse.
     * */
    public GlobalStatsResponse getStats() {
    	return new GlobalStatsResponse(inputTokens.sum(), outputTokens.sum());
    }

}
