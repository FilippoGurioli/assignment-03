let btnLights = document.getElementById("lights");
let rngBlinds = document.getElementById("blinds");
let btnControls = document.getElementById("btnControls");
let btnHistory = document.getElementById("btnHistory");

setup("ON", 90);

btnLights.addEventListener("click", function(event){
    if(btnLights.value == "OFF"){
        btnLights.value = "ON";
    } else{
        btnLights.value = "OFF";
    }
});

rngBlinds.addEventListener("change", function(event){
    document.querySelector("div p").innerHTML = rngBlinds.value;
});

btnControls.addEventListener("click", function(event){
    document.getElementById("controls").style.display = "flex";
    document.getElementById("state").style.display = "none";
});

btnHistory.addEventListener("click", function(event){
    document.getElementById("controls").style.display = "none";
    document.getElementById("state").style.display = "flex";
});

function setup(light, degrees) {
    btnLights.value = light;
    rngBlinds.value = degrees;
    document.querySelector("div p").innerHTML = rngBlinds.value;
}