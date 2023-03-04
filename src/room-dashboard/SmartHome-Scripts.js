let btnLights = document.getElementById("lights");
let rngBlinds = document.getElementById("blinds");
let btnControls = document.getElementById("btnControls");
let btnAuto = document.getElementById("btnAuto");
let btnHistory = document.getElementById("btnHistory");
let auto = true;

//Setup
updateCurrentState();

//Controls' event listeners
btnLights.addEventListener("click", function(event){
    event.preventDefault();
    if (auto) {
        disableAuto();
    }
    if (btnLights.value == "OFF") {
        btnLights.value = "ON";
        btnLights.style.backgroundImage = "linear-gradient(to bottom right, #22c1c3, #fdbb2d)";
        sendUpdate("lights", "ON");
    } else {
        btnLights.value = "OFF";
        btnLights.style.backgroundImage = null;
        sendUpdate("lights", "OFF");
    }
});

rngBlinds.addEventListener("change", function(event){
    if (auto) {
        disableAuto();
    }
    event.preventDefault();
    document.querySelector("div p").innerHTML = rngBlinds.value;
    sendUpdate("blinds", rngBlinds.value.toString());
});

function autoMode(event){
    event.preventDefault();
}

function noAutoMode(event){
    event.preventDefault();
    enableAuto();
}

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
    } else if (key == "master") {
        values = {'type' : "master"};
    }
	var jsonValues = JSON.stringify(values);

    //Sends POST request with JSON message
    axios.post('http://localhost:8080/api/data', jsonValues).then(response => {
		console.log("Sent correctly.");
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

//TO-DO?
function updateCurrentState(){
    axios.get('http://localhost:8080/api/currentData').then(response => {
        let data = response.data;
        btnLights.value = data["light"];
        if (data["light"].equals("ON")) {
            btnLights.style.backgroundImage = "linear-gradient(to bottom right, #22c1c3, #fdbb2d)";
        }
        rngBlinds.value = data["degrees"];
        let master = data["master"];
        if (master == "DASH") {
            auto = false;
            btnAuto.style.border = "4px solid transparent";
            btnAuto.style.boxShadow = "0px 0px 20px rgba(255, 255, 255, 0.2)";
            btnAuto.addEventListener("click", btnAuto.func=(ev)=>noAutoMode(ev));
        } else {
            auto = true;
            btnAuto.style.border = "4px solid #22c1c3";
            btnAuto.style.boxShadow = "0px 0px 20px transparent";
            btnAuto.addEventListener("click", btnAuto.func=(ev)=>autoMode(ev));
        }
        document.querySelector("div p").innerHTML = rngBlinds.value;
    });
}

function enableAuto(){
    askPriority();
    btnAuto.style.border = "4px solid #22c1c3";
    btnAuto.style.boxShadow = "0px 0px 20px transparent";
    auto = true;
    btnAuto.removeEventListener("click", btnAuto.func);
    btnAuto.addEventListener("click", btnAuto.func=(ev)=>autoMode(ev));
}

function disableAuto(){
    askPriority();
    btnAuto.style.border = "4px solid transparent";
    btnAuto.style.boxShadow = "0px 0px 20px rgba(255, 255, 255, 0.2)";
    auto = false;
    btnAuto.removeEventListener("click", btnAuto.func);
    btnAuto.addEventListener("click", btnAuto.func=(ev)=>noAutoMode(ev));
}

function askPriority(){
    sendUpdate("master", null);
}