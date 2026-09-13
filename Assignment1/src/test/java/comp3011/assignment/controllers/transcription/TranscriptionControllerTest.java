package comp3011.assignment.controllers.transcription;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment.controllers.TranscriptionController;
import comp3011.assignment.services.TranscriptionService;

@WebMvcTest(TranscriptionController.class)
public class TranscriptionControllerTest {

	@Autowired 
    MockMvc mockMvc;
    
    @MockitoBean 
    TranscriptionService transcriptionService;
    
    @Test
    @DisplayName("POST the transcribe endpoint and return the correct 200 shape")
    void transcriptionReturn200Shape() throws Exception{
    	when(transcriptionService.transcribe(any())).thenReturn("This meeting is really important");
    	var file = new MockMultipartFile("file", "a.webm", "audio/webm", "fake audio".getBytes());
    	mockMvc.perform(multipart("/api/v1/transcribe").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.text").value("This meeting is really important"));
    }
    
    @Test
    @DisplayName("POST the transcribe endpoint and return the correct 500 shape")
    void transcriptionReturn500shape() throws Exception{
    	when(transcriptionService.transcribe(any())).thenThrow(new RuntimeException("Error!!"));
    	var file = new MockMultipartFile("file", "a.webm", "audio/webm", "fake audio".getBytes());
    	mockMvc.perform(multipart("/api/v1/transcribe").file(file))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.path").value("/api/v1/transcribe"));
    }
    
}
