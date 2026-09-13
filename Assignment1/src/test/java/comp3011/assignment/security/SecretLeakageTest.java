package comp3011.assignment.security;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * Makes sure the OpenAI key never leaks.
 *
 * The app starts with a fake key, then we send a transcription request that
 * fails on purpose. If the key shows up in the logs or in the response, the
 * test fails. This is the case that matters most for the security requirement.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "OPENAI_API_KEY=" + SecretLeakageTest.FAKE_KEY,
        "spring.profiles.active="   // ignore the local profile so the fake key is used
    })
@ExtendWith(OutputCaptureExtension.class)
class SecretLeakageTest {

    static final String FAKE_KEY = "sk-test-DO-NOT-SHOW-THIS-123";

    @LocalServerPort
    int port;

    @Test
    @DisplayName("Transcription failure does not leak the API key")
    void apiKeyNeverLeaksToLogsOrResponse(CapturedOutput output) {
        String url = "http://localhost:" + port + "/api/v1/transcribe";
        RestClient http = RestClient.create();

        // A dummy audio upload, which can call a fail
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("fake audio".getBytes()) {
            @Override
            public String getFilename() {
                return "audio.webm";
            }
        });

        // Sending the audio and the OpenAI call fails,  which is exactly the path
        // where a careless log line could expose the key.
        String responseBody;
        try {
            responseBody = http.post().uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            responseBody = e.getMessage();
        }

        // Nothing in the logs should contain the key.
        assertFalse(output.getAll().contains(FAKE_KEY), "API key must never be written to logs");
        assertFalse(output.getAll().contains("Bearer " + FAKE_KEY),"Bearer token must never be written to logs");

        // And the client should never see it either.
        if (responseBody != null) {
            assertFalse(responseBody.contains(FAKE_KEY),"API key must never be returned to the client");
        }
    }
}