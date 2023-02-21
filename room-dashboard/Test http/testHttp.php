<?php
    $result["Data"]="19-10-2022";
    $result["Ora"]="15:02";
    $result["Azione"]="Luci accese";
    if(isset($_POST["Ciao"])){
        $result["Ciao"]=$_POST["Ciao"];
    }
    header('Content-Type: application/json');
	echo json_encode($result);
?>