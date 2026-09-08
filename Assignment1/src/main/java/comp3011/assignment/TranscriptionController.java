package comp3011.assignment;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class TranscriptionController {
    private final TranscriptionService service;
    public TranscriptionController(TranscriptionService service) { this.service = service; }

    @PostMapping("/transcribe")
    public Map<String, String> transcribe(@RequestParam("file") MultipartFile file) throws IOException {
        return Map.of("text", service.transcribe(file));
    }
}