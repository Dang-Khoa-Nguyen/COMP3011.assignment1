package comp3011.assignment.controllers.globalstats;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment.components.responses.GlobalStatsResponse;
import comp3011.assignment.controllers.GlobalStatsController;
import comp3011.assignment.services.TokenCounterService;

@WebMvcTest(GlobalStatsController.class)
class GlobalStatsControllerTest {
    
	@Autowired 
    MockMvc mockMvc;
    
    @MockitoBean 
    TokenCounterService tokenCounterService;

    @Test
    @DisplayName("GET the global/stats endpoint and return the correct shape")
    void statsReturnsCorrectShape() throws Exception {
        when(tokenCounterService.getStats())
            .thenReturn(new GlobalStatsResponse(100, 20));
        mockMvc.perform(get("/api/v1/global/stats"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.inputTokens").value(100))
               .andExpect(jsonPath("$.outputTokens").value(20));
    }
}