let btnLights = document.getElementById("lights");
let rngBlinds = document.getElementById("blinds");

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

function setup(light, degrees) {
    btnLights.value = light;
    rngBlinds.value = degrees;
    document.querySelector("div p").innerHTML = rngBlinds.value;
}