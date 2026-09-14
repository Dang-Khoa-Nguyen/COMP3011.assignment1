package comp3011.assignment.controllers.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment.components.shutdown.ApplicationTerminator;
import comp3011.assignment.controllers.AdministrationController;

@WebMvcTest(AdministrationController.class)
public class UpTimeTest {

    @Autowired
    private MockMvc mockMvc;
    
    // To avoid a mock having a real shutdown.
    @MockitoBean
    private ApplicationTerminator terminator;

    /**
     * Verifies /api/v1/admin/uptime endpoint returns 200 with the three fields that the YAML requires
     * 
     * In addition, the uptime is a non-negative number. However, exact timestamps can't be
     * asserted as they change every call. Therefore, only checking the shape and
     * the up time seconds must be greater or equal to 0.0
     */
    @Test
    void uptimeReturns200Shape() throws Exception {
        mockMvc.perform(get("/api/v1/admin/uptime"))
               .andExpect(status().isOk())                                  
               .andExpect(jsonPath("$.utcServerStart").exists())  
               .andExpect(jsonPath("$.utcNow").exists())       
               .andExpect(jsonPath("$.serverUptimeSeconds").exists())       
               .andExpect(jsonPath("$.serverUptimeSeconds").value(greaterThanOrEqualTo(0.0)));
    }
}