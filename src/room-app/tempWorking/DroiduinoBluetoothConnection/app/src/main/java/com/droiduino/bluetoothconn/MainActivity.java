package com.droiduino.bluetoothconn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // UI Initialization
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        final TextView textViewInfo = findViewById(R.id.textViewInfo);
        final Switch switchToggle = findViewById(R.id.switch1);
        final Slider main_slider = findViewById(R.id.main_slider);
        final TextView textViewBlinds = findViewById(R.id.textViewBlinds);
        textViewBlinds.setEnabled(false);
        main_slider.setEnabled(false);
        switchToggle.setEnabled(false);
        switchToggle.setText(switchToggle.getTextOff());
        final ImageView imageView = findViewById(R.id.imageView);
        imageView.setBackgroundColor(getResources().getColor(R.color.colorOff));



        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progress and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                connectedThread.write("UPDATE\n");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                switchToggle.setEnabled(true);
                                main_slider.setEnabled(true);
                                textViewBlinds.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!QUI PER MODIFICARE ARDUINO->ANDROID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        arduinoMsg = arduinoMsg.replaceAll("[^a-zA-Z0-9_-]", "");
                        if (arduinoMsg.equals("ON")) {
                            imageView.setBackgroundColor(getResources().getColor(R.color.colorOn));
                            textViewInfo.setText("Arduino Message : " + arduinoMsg);
                            switchToggle.setChecked(true);
                        } else if (arduinoMsg.equals("OFF")) {
                            imageView.setBackgroundColor(getResources().getColor(R.color.colorOff));
                            textViewInfo.setText("Arduino Message : " + arduinoMsg);
                            switchToggle.setChecked(false);
                        } else {
                            main_slider.setValue(Integer.parseInt(arduinoMsg));
                            textViewInfo.setText("Arduino Message - Servo: " + arduinoMsg);
                        }
                }
            }
    };

    // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Move to adapter list
            Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
            startActivity(intent);
        }
    });

    // Slider for blinds control
        main_slider.addOnChangeListener(new Slider.OnChangeListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            connectedThread.write(String.valueOf(value) + "\n");
        }
    });

    // Button to ON/OFF LED on Arduino Board
        switchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String cmdText = null;

            if (isChecked) {
                switchToggle.setText(switchToggle.getTextOn());
                // Command to turn on LED on Arduino. Must match with the command in Arduino code
                cmdText = "ON\n";
            } else {
                switchToggle.setText(switchToggle.getTextOff());
                // Command to turn off LED on Arduino. Must match with the command in Arduino code
                cmdText = "OFF\n";
            }
            // Send command to Arduino board
            connectedThread.write(cmdText);
        }
    });
}

/* ============================ Thread to Create Bluetooth Connection =================================== */
public static class CreateConnectThread extends Thread {

    public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        BluetoothSocket tmp = null;
        UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

        try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
            tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.e("Status", "Device connected");
            handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
                Log.e("Status", "Cannot connect to device");
                handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.run();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}

/* =============================== Thread for Data Transfer =========================================== */
public static class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                buffer[bytes] = (byte) mmInStream.read();
                String readMessage;
                if (buffer[bytes] == '\n'){
                    readMessage = new String(buffer,0,bytes);
                    Log.e("Arduino Message",readMessage);
                    handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                    bytes = 0;
                } else {
                    bytes++;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        byte[] bytes = input.getBytes(); //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("Send Error","Unable to send message",e);
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
