# TESTING
All tests run on every build and guard against breaking existing behaviour, with 10 tests in total.
Tests that would call OpenAI use a stub, so they are fast, free, and need no API key.

## Running the tests
```bash
./mvnw test
```

## Concurrency
| Test | What it proves |
|------|----------------|
| `TokenCounterTest.concurrentUpdatesCountCorrectly` | 200 threads update the counter at the same instant; totals must be exact, proving no updates are lost (thread-safe). |
| `ConcurrencyRequestLoadTest.handles250ConcurrentBlockingRequests` | 250 requests, each blocking 500ms via a stub, fire at once and finish in a few seconds instead of nearly 125s serial, which proves 200+ concurrent blocking requests are handled. |

## Controllers (stubbed STT with no real OpenAI call)
| Test | What it proves |
|------|----------------|
| `TranscriptionControllerTest.transcriptionReturn200Shape` | A valid upload returns `200` with the transcript. |
| `TranscriptionControllerTest.transcriptionReturn400Shape` | A request with no file returns the `400` error shape. |
| `TranscriptionControllerTest.transcriptionReturn500Shape` | A service failure returns the `500` error shape. |
| `GlobalStatsControllerTest.statsReturns200ShapeWithCorrectTokens` | The stats endpoint returns the correct JSON shape and values. |
| `UpTimeTest.uptimeReturns200Shape` | The uptime endpoint returns `200` with the three required fields and a non-negative uptime. |
| `ShutdownTest.secondShutdownReturns409Conflict` | First shutdown returns `202`, a second returns `409`, and shutdown fires exactly once. |

## Security
| Test | What it proves |
|------|----------------|
| `SecretLeakageTest.apiKeyNeverLeaksToLogsOrResponse` | With a fake key, a failed transcription is triggered and the key doesn't appear in the logs or the response. |