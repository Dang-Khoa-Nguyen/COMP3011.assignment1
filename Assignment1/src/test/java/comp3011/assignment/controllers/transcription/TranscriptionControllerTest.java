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
    @DisplayName("POST the transcribe endpoint and return the correct 200 shape, meaning the transcription works well")
    void transcriptionReturn200Shape() throws Exception{
    	when(transcriptionService.transcribe(any())).thenReturn("This meeting is really important");
    	var file = new MockMultipartFile("file", "a.webm", "audio/webm", "fake audio".getBytes());
    	mockMvc.perform(multipart("/api/v1/transcribe").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.text").value("This meeting is really important"));
    }
    
    @Test
    @DisplayName("POST the transcribe endpoint and return the correct 400 shape, meaining the file is missing")
    void transcriptionReturn400Shape() throws Exception{
    	when(transcriptionService.transcribe(any())).thenThrow(new RuntimeException("Bad Request"));
    	mockMvc.perform(multipart("/api/v1/transcribe"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("A 'file' part is required."))
        .andExpect(jsonPath("$.path").value("/api/v1/transcribe"));
    }
    
    @Test
    @DisplayName("POST the transcribe endpoint and return the correct 500 shape, meaning the transcription has errors.")
    void transcriptionReturn500Shape() throws Exception{
    	when(transcriptionService.transcribe(any())).thenThrow(new RuntimeException("Internal Server Error"));
    	var file = new MockMultipartFile("file", "a.webm", "audio/webm", "fake audio".getBytes());
    	mockMvc.perform(multipart("/api/v1/transcribe").file(file))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("An unexpected server error occurred."))
        .andExpect(jsonPath("$.path").value("/api/v1/transcribe"));
    }
    
}
