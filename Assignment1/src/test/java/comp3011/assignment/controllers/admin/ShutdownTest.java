package comp3011.assignment.controllers.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment.components.ApplicationTerminator;
import comp3011.assignment.controllers.AdministrationController;

@WebMvcTest(AdministrationController.class)
@DirtiesContext
public class ShutdownTest {

	 	@Autowired
	    private MockMvc mockMvc;
	 	
	 	 // To avoid a mock having a real shutdown.
	 	 @MockitoBean
	     private ApplicationTerminator terminator;

	    /**
	     * Verifies the shutdown endpoint's error handling
	     * 
	     * the FIRST call is accepted (202), 
	     * but a SECOND call while shutdown is in progress must be rejected with 409 Conflict 
	     * and the ErrorResponse shape defined in the YAML.
	     */
	    @Test
	    void secondShutdownReturns409Conflict() throws Exception {
	        // First call should return 202 
	        mockMvc.perform(post("/api/v1/admin/shutdown"))
	               .andExpect(status().isAccepted())                      
	               .andExpect(jsonPath("$.message").value("Graceful shutdown requested."));

	        // Second call should return 409 as the progress is working.
	        mockMvc.perform(post("/api/v1/admin/shutdown"))
	               .andExpect(status().isConflict())                       
	               .andExpect(jsonPath("$.status").value(409))
	               .andExpect(jsonPath("$.error").value("Conflict"))
	               .andExpect(jsonPath("$.message").value("Graceful shutdown is already in progress."))
	               .andExpect(jsonPath("$.path").value("/api/v1/admin/shutdown"));
	    }
	}
