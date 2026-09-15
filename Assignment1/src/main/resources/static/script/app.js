

let recorder, chunks = [];
	
// Get button id
const recordBtn = document.getElementById("button-record");
const recordingBtn = document.getElementById("button-recording");
const errorMessage = document.getElementById("result-error");
const processingBtn = document.getElementById("processing");

/*
* Start the recording (hide record button and show recording button)
*/
async function startRecording() {
	try {
		const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
		
		recorder = new MediaRecorder(stream, {
            audioBitsPerSecond: 16000
        });

		chunks = [];

		// collect audio as it arrives
		recorder.ondataavailable = e => chunks.push(e.data);  
		
		// upload once stopped
		recorder.onstop = sendAudio;                           
		recorder.start();
		
		errorMessage.classList.add("hidden");
		recordBtn.classList.add("hidden")     
		recordingBtn.classList.remove("hidden");
		}
	catch (e) {
		document.getElementById("result").textContent = "Microphone access denied ";
	}

}

/*
* Stop the recording (hide recording button and show record button
*/
function stopRecording() {
	recorder.stop();
	recorder.stream.getTracks().forEach(t => t.stop());
	
	recordBtn.classList.remove("hidden")      
    recordingBtn.classList.add("hidden");    
}

/**
 * Sending audio to the endpoint /transcribe
 */
async function sendAudio() {		
	try {
		processingBtn.classList.remove("hidden");
		
		// MediaRecorder default
		const blob = new Blob(chunks, { type: 'audio/webm' });  
		const form = new FormData();
		  
		// filename matters
		form.append('file', blob, 'audio.webm');               
		
		// Fetch the /transcribe endpoint and send the audio to the endpoint.
		const res = await fetch('/api/v1/transcribe', { method: 'POST', body: form });
		
		// If the status is not ok, throws error and message depending on the status to let user know 
		// what errors they are facing.
		if (!res.ok) {
           let message;
           switch (res.status) {
               case 400: 
				   message = "No audio was received. Please record first."; 
				   break;
               case 413: 
					message = "That recording is too large. Try a shorter clip."; 
					break;
               default:  
			   		message = "Transcription failed on the server. Please try again."; 
					break;
           }
           document.getElementById('result-error').textContent = message;
		   errorMessage.classList.remove("hidden");
           return;
       }

		const data = await res.json();
		document.getElementById('result').textContent = data.text; 
		
	  } catch(e) {
		document.getElementById('result-error').textContent = "Could not reach the server. Check your connection and try again.";
		errorMessage.classList.remove("hidden");
	  } finally {
		processingBtn.classList.add("hidden");
	    recordBtn.classList.remove("hidden");
	  }
}