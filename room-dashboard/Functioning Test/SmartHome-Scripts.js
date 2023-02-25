let btnLights = document.getElementById("lights");
let rngBlinds = document.getElementById("blinds");
let btnControls = document.getElementById("btnControls");
let btnHistory = document.getElementById("btnHistory");

setup("ON", 90);

//Controls event listeners
btnLights.addEventListener("click", function(event){
    event.preventDefault();
    if(btnLights.value == "OFF"){
        btnLights.value = "ON";
        sendUpdate("lights", "ON");
    } else{
        btnLights.value = "OFF";
        sendUpdate("lights", "OFF");
    }
});

rngBlinds.addEventListener("change", function(event){
    document.querySelector("div p").innerHTML = rngBlinds.value;
    sendUpdate("blinds", rngBlinds.value.toString());
});

//Navigation
btnControls.addEventListener("click", function(event){
    document.getElementById("controls").style.display = "flex";
    document.getElementById("state").style.display = "none";
});

btnHistory.addEventListener("click", function(event){
    document.getElementById("controls").style.display = "none";
    document.getElementById("state").style.display = "flex";
});

//Functions
function setup(light, degrees) {
    //axios.get('http://localhost:8080/api/data').then(response => {
	//	console.log("Getted correctly.");
    //});
    btnLights.value = light;
    rngBlinds.value = degrees;
    document.querySelector("div p").innerHTML = rngBlinds.value;
}

function sendUpdate(key, value){
    //const formData = new FormData();
    //formData.append(key, value);
    let values;
    if (key == "lights") {
        values = {'lights' : value};
    } else if (key == "blinds") {
        values = {'blinds' : value};
    }
	var jsonValues = JSON.stringify(values);
    axios.post('http://localhost:8080/api/data', jsonValues).then(response => {
		console.log("Sended correctly.");
    });
}