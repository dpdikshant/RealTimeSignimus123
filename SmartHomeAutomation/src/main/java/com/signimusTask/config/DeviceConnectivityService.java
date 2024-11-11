
package com.signimusTask.config;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
public class DeviceConnectivityService {

    // Autowiring MQTT services
    @Autowired
    private MqttConfig mqttConfig;

    // Bluetooth server UUID for Bluetooth communication
    private static final String BLUETOOTH_SERVER_UUID = "btspp://localhost:0000110100001000800000805F9B34FB;name=DeviceService";

    /**
     * Initiates Bluetooth communication by starting a server to listen for incoming connections.
     */
    public void startBluetoothServer() {
        try {
            StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(BLUETOOTH_SERVER_UUID);
            System.out.println("Bluetooth server started. Waiting for clients to connect...");

            // Waiting for a connection
            StreamConnection connection = notifier.acceptAndOpen();
            System.out.println("Bluetooth client connected.");

            // Handling Bluetooth communication
            handleBluetoothConnection(connection);
        } catch (IOException e) {
            System.err.println("Bluetooth server error: " + e.getMessage());
        }
    }

    /**
     * Handles Bluetooth communication by reading and writing data from/to the connected Bluetooth client.
     */
    private void handleBluetoothConnection(StreamConnection connection) {
        try (InputStream inStream = connection.openInputStream();
             OutputStream outStream = connection.openOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                System.out.println("Received Bluetooth message: " + message);

                // Example action based on Bluetooth message
                if (message.equalsIgnoreCase("ON")) {
                    System.out.println("Turning device ON via Bluetooth");
                    outStream.write("Device turned ON".getBytes());
                } else if (message.equalsIgnoreCase("OFF")) {
                    System.out.println("Turning device OFF via Bluetooth");
                    outStream.write("Device turned OFF".getBytes());
                }
            }
        } catch (IOException e) {
            System.err.println("Bluetooth connection error: " + e.getMessage());
        }
    }

    /**
     * Connects to the MQTT broker for Wi-Fi-enabled devices.
     * This will use the MQTT topics already set up in the current MQTT configuration.
     */  public void connectToWifiDevice() {
         try {
             String clientId = mqttConfig.getClientId(); // Assuming a getter for clientId
             String brokerUrl = mqttConfig.getBrokerUrl(); // Assuming a getter for brokerUrl
             mqttConfig.mqttClientFactory().getClientInstance(clientId, brokerUrl).connect();
             MqttClient client = mqttConfig.mqttClient();
             if (!client.isConnected()) {  // Check if already connected
                 client.connect();
                     System.out.println("Connected to Wi-Fi device using MQTT.");
             } else {
                 System.out.println("Already connected to Wi-Fi device.");
             }
         } catch (MqttException e) {
             System.err.println("Error connecting to Wi-Fi device: " + e.getMessage());
         }
     }
    /**
     * Sends control commands to Wi-Fi (MQTT-enabled) devices.
     * 
     * @param topic   MQTT topic to publish to
     * @param message Message or command to be sent (e.g., "ON" or "OFF")
     */
    public void controlWifiDevice(String topic, String message) {
        try {
            mqttConfig.deviceControlOutputChannel().send(new GenericMessage<>(message));
            System.out.println("Sent message to Wi-Fi device via MQTT: " + message);
        } catch (Exception e) {
            System.err.println("Error sending Wi-Fi command: " + e.getMessage());
        }
    }
}

