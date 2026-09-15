# Voice Transcription
 
A Spring Boot web application that converts speech to tex. Users record audio in
the browser, and the app transcribes it using OpenAI's speech-to-text API
(`gpt-4o-mini-transcribe`) so they can review meetings or personal recordings and
take notes afterwards. In addition, the application also records the token through endpoints defined in assignment1api.yaml.
 
The browser never contacts OpenAI directly. The backend acts as a **secure proxy**:
it holds the API key, forwards the audio, and returns only the transcribed text, so
the key is never exposed to the client.

 
# Quick start in local
 
```bash
# set your OpenAI key in Mac / Linux
export OPENAI_API_KEY=sk-your-key       
./mvnw spring-boot:run                 

# set your OpenAI key in Windows 
set OPENAI_API_KEY=sk-your-key
mvnw spring-boot:run
```

Open http://localhost:8080, record, and the transcription appears on the page.

Reading here to understand clearly how to set up -> **[Setup & Configuration](Assignment1/docs/SETUP.md)**

## Endpoints
 
| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `/api/v1/transcribe` | Upload audio, return the transcription |
| `GET`  | `/api/v1/admin/uptime` | Report server start time, current time, and uptime |
| `GET`  | `/api/v1/global/stats` | Report cumulative token usage since startup |
| `POST` | `/api/v1/admin/shutdown` | Request a graceful shutdown |

Full contract for all endpoints is in the YAML spec. Only the /transcribe endpoint is additional as it can call the transcription service to accept an audio upload and return the transcription.

The /transcribe have three handlers, including 400 for missing file, 413 for large file, and 500 for an unexpected server failure. This can return to the client for easy to know what kinds of errors that they are facing.

# Concurrency handling

The requirement is to handle 200+ simultaneous requests, each of which blocks while
waiting on the OpenAI call. Two things make this work:
 
- **Virtual threads** (`spring.threads.virtual.enabled=true`). Each transcription
  blocks for a second or two waiting on OpenAI. With ordinary platform threads, a
  few hundred concurrent requests would exhaust the thread pool. Virtual threads
  make blocking cheap, so the app can hold hundreds of blocked requests at once
  without stalling.
- **Thread-safe shared state.** The token counter uses `LongAdder`, which is built
  for high-contention writes and guarantees no updates are lost when many requests
  increment it at the same time. The shutdown flag uses `AtomicBoolean` so that,
  even under simultaneous shutdown requests, only one is accepted.

This is verified by two tests: a race-condition test on the counter
(`TokenCounterTest`) and a 250-request blocking-load test
(`ConcurrencyRequestLoadTest`). This demonstrates in the test documentation.
 
# Additional Documentation
| Path documentation | Description |
|--------|------|
|[Architecture](Assignment1/docs/ARCHITECTURE.md)|  package layout, design decisions, and request flow|
|[Testing](Assignment1/docs/TESTING.md) | how to run the tests and what each one proves |
|[AI Usage](Assignment1/docs/AIUSAGE.md)| how AI tools were used during development |

