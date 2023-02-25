<?php
    session_start();
    //header("Access-Control-Allow-Origin: http://localhost:8080/api/data");
    define("UPLOAD_DIR", "./upload/");
    define("DEFAULT_DIR", "");
?>

<!DOCTYPE html>
<html lang="it">
    <head>
        <title>SmartHome - Dashboard</title>
        <meta charset="utf-8">
        <link rel="stylesheet" href="SmartHome-Style.css">
    </head>
    <body>
        <header>
            <h1>SmartHome Dashboard</h1>
        </header>
        <nav>
            <div id="btnControls">Controlli</div>
            <div id="btnHistory">Storico</div>
        </nav>
        <section id="controls">
            <form>
                <div>
                    <input type="button" name="btnLights" id="lights" value="OFF">
                    <label for="lights">Luci</label>
                </div>
                <div>
                    <p>0</p>
                    <input type="range" name="rngBlinds" id="blinds" min="0" max="180" value="0">
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
                    <td>Tende aperte al 10% </td>
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
            </table>
        </section>
        <footer>
            <p>By Silvia Furegato, Filippo Gurioli & Tommaso Turci.</p>
        </footer>
        <script src="SmartHome-Scripts.js"></script>
        <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    </body>
</html>