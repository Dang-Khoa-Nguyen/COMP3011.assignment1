
# SET UP & CONFIGURATION
## Providing the API key 
The app reads the key from the `OPENAI_API_KEY` property. Where it comes from depends
on the environment:
 
| Environment | Key source |
|-------------|-----------|
| TITAN | `OPENAI_API_KEY` environment variable (provided by TITAN) |
| Local run | `src/main/resources/application-local.properties` (gitignored) |
| Tests | `src/test/resources/application.properties` (a dummy key) |
 
The key application-local.properties` is never committed and in gitignored,
and the key is only read at runtime
 
### Option A — environment variable
```bash
# Mac / Linux
export OPENAI_API_KEY=sk-your-key
```
```cmd
:: Windows
set OPENAI_API_KEY=sk-your-key
```
 
### Option B — local properties file
Create `src/main/resources/application-local.properties`:
```properties
OPENAI_API_KEY=sk-your-key
```
This file is gitignored, so the key never enters the repository.
 
## Running locally
 
```bash
 # Mac / Linux
./mvnw spring-boot:run 

# Windows
mvnw spring-boot:run       
```
 
The app starts at http://localhost:8080. If the key is missing, the app fails to
start with a clear message (`OPENAI_API_KEY is not set`).

## Building the runnable JAR
 
```bash
./mvnw clean package
java -jar target/Assignment1-0.0.1-SNAPSHOT.jar
```
 
This produces a single executable JAR containing all dependencies.
 
## Running the tests
 
```bash
./mvnw test
```
 
Tests use a dummy key from `src/test/resources/application.properties`, so they run
without a real OpenAI key and make no external calls, which means the transcription service is
stubbed.
 
## Configuration reference
 
Set in `src/main/resources/application.properties`:
 
| Setting | Value | Reason |
|---------|-------|--------|
| `spring.threads.virtual.enabled` | `true` | Handle 200+ blocking requests cheaply |
| `server.shutdown` | `graceful` | Let in-flight requests finish before shutting down |
| `spring.servlet.multipart.max-file-size` | `25MB` | Matches OpenAI's audio upload limit |
| `spring.servlet.multipart.max-request-size` | `25MB` | Same limit for the whole request |