

let recorder, chunks = [];
	
// Get button id
const recordBtn = document.getElementById("button-record");
const recordingBtn = document.getElementById("button-recording");

// hide record and show recording
async function startRecording() {
	try {
		const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
		recorder = new MediaRecorder(stream);
		chunks = [];

		// collect audio as it arrives
		recorder.ondataavailable = e => chunks.push(e.data);  
		
		// upload once stopped
		recorder.onstop = sendAudio;                           
		recorder.start();
		
		recordBtn.classList.add("hidden")     
		recordingBtn.classList.remove("hidden");
		}
	catch (e) {
		document.getElementById("result").textContent = "Microphone access denied ";
	}

}

// hide recording and show record
function stopRecording() {
	recorder.stop();
	recorder.stream.getTracks().forEach(t => t.stop());
	
	recordBtn.classList.remove("hidden")      
    recordingBtn.classList.add("hidden");    
}


async function sendAudio() {
const recordBtn = document.getElementById("button-record");
const processingBtn = document.getElementById("button-processing");
	
try {
	processingBtn.classList.remove("hidden");
	
	// MediaRecorder default
	const blob = new Blob(chunks, { type: 'audio/webm' });  
	const form = new FormData();
	  
	// filename matters
	form.append('file', blob, 'audio.webm');               
	
	// display it
	const res = await fetch('/api/v1/transcribe', { method: 'POST', body: form });
	const data = await res.json();
	document.getElementById('result').textContent = data.text; 
  } catch(e) {
	document.getElementById('result').textContent = "Unavailable to save the audio";
  } finally {
	processingBtn.classList.add("hidden");
    recordBtn.classList.remove("hidden");
  }
}