<?php
    session_start();
    $_SERVER["REQUEST_METHOD"] = 'POST';
    header("Access-Control-Allow-Origin: http://localhost:80/assignment-03/room-dashboard/Functioning%20Test/Smarthome-Dashboard.php");

    $currentValues["light"] = "OFF";
    $currentValues["blinds"] = "0";

    $entityBody = file_get_contents('php://input');
    //var_dump($entityBody);

    if (isset($_POST["lightVal"])) {
        $currentValues["light"] = $_POST["lightVal"];
        echo "ciao";
        //header("HTTP/1.1 200 OK");
    }
    if (isset($_POST["blindsVal"])) {
        $currentValues["blinds"] = $_POST["blindsVal"];
        //header("HTTP/1.1 200 OK");
        var_dump($_POST);
    }
?>

<!DOCTYPE html>
<html lang="it">
    <head>
        <title>SmartHome - Dashboard</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta charset="utf-8">
        <link rel="stylesheet" href="SmartHome-Style.css">
    </head>
    <body>
        <header>
            <h1>SmartHome Dashboard</h1>
        </header>
        <nav>
            <div id="btnControls">Controlli</div>
            <div id="btnAuto">Auto</div>
            <div id="btnHistory">Storico</div>
        </nav>
        <section id="controls">
            <form>
                <div>
                    <input type="button" name="btnLights" id="lights" value="<?php echo $currentValues["light"]; ?>">
                    <label for="lights">Luci</label>
                </div>
                <div>
                    <p><?php echo $currentValues["blinds"]; ?></p>
                    <input type="range" name="rngBlinds" id="blinds" min="0" step="30" max="180" value="0">
                    <label for="blinds">Tende</label>
                </div>
            </form>
        </section>
        <section id="state">
            <table>
                <tr>
                    <th>Giorno</th>
                    <th>Ora</th>
                    <th></th>
                </tr>
                <tr>
                    <td>23/11/2022</td>
                    <td>11:32</td>
                    <td>Tende aperte al 35% </td>
                </tr>
                <tr>
                    <td>18/08/2022</td>
                    <td>05:32</td>
                    <td>Luci accese</td>
                </tr>
                <tr>
                    <td>12/06/2022</td>
                    <td>18:32</td>
                    <td>Luci spente</td>
                </tr>
                <tr>
                    <td>23/11/2022</td>
                    <td>11:32</td>
                    <td>Tende aperte al 65% </td>
                </tr>
            </table>
        </section>
        <footer>
            <p>By Silvia Furegato, Filippo Gurioli & Tommaso Turci.</p>
        </footer>
        <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
        <script src="SmartHome-Scripts.js"></script>
    </body>
</html>