// The class is used to track total token usage since server start

package comp3011.assignment.controllers;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import org.springframework.stereotype.Component;

@Component
public class StatsHolder {

	// LongAdder handles concurrent updates safely under heavy load.
    private final LongAdder inputTokens = new LongAdder();
    private final LongAdder outputTokens = new LongAdder();

    // Adds the token counts from a transcription response.
    public void record(Map<?, ?> response) {
        Object usage = response.get("usage");
        if (usage instanceof Map<?, ?> u) {
            inputTokens.add(toLong(u.get("input_tokens"))); 
            outputTokens.add(toLong(u.get("output_tokens")));
        }
    }
    
    // Converts a Number to long, or returns 0 if missing
    private long toLong(Object n) {
        return n instanceof Number num ? num.longValue() : 0;
    }
    
    // return the inputTokens and outTokens
    public long input()  { return inputTokens.sum(); }
    public long output() { return outputTokens.sum(); }
}