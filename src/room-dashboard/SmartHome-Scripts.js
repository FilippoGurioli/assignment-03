let btnLights = document.getElementById("lights");
let rngBlinds = document.getElementById("blinds");
let btnControls = document.getElementById("btnControls");
let btnHistory = document.getElementById("btnHistory");

//Setup
updateCurrentState();

//Controls' event listeners
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
    updateCurrentState();
    document.getElementById("controls").style.display = "flex";
    document.getElementById("state").style.display = "none";
});

btnHistory.addEventListener("click", function(event){
    updateHistory();
    document.getElementById("controls").style.display = "none";
    document.getElementById("state").style.display = "flex";
});

//Functions

function sendUpdate(key, value) {
    //Builds JSON message
    let values;
    if (key == "lights") {
        values = {'type' : "light", 'value' : value};
    } else if (key == "blinds") {
        values = {'type' : "blind", 'value' : value};
    }
	var jsonValues = JSON.stringify(values);

    //Sends POST request with JSON message
    axios.post('http://localhost:8080/api/data', jsonValues).then(response => {
		console.log("Sended correctly.");
    });
}

function updateHistory() {
    //Sends GET request and populates the HTML with response data
    axios.get('http://localhost:8080/api/data').then(response => {
        let allRecords = response.data;
        let record = `<tr>
                        <th>Giorno</th>
                        <th>Ora</th>
                        <th></th>
                      </tr>`;
        for(let i = 0; i < allRecords.length; i++) {
            record += `<tr>
                            <td>${allRecords[i]["Date"]}</td>
                            <td>${allRecords[i]["Time"]}</td>
                            <td>${allRecords[i]["Content"]}</td>
                        </tr>`;
        }
        const main = document.querySelector("#state table");
        main.innerHTML = record;
    });
}

//TO-DO
function updateCurrentState(){
    axios.get('http://localhost:8080/api/currentData').then(response => {
        let data = response.data;
        btnLights.value = data["light"];
        rngBlinds.value = data["degrees"];
        document.querySelector("div p").innerHTML = rngBlinds.value;
    });
}