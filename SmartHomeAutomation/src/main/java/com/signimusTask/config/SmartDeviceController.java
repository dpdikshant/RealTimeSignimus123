
package com.signimusTask.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmartDeviceController {

    @Autowired
    private DeviceConnectivityService connectivityService;

    public void initializeConnections() {
        // Start Bluetooth server
        connectivityService.startBluetoothServer();

        // Connect to Wi-Fi MQTT broker
        connectivityService.connectToWifiDevice();
    }

    public void controlDevice(String deviceType, String command) {
        if ("wifi".equalsIgnoreCase(deviceType)) {
            connectivityService.controlWifiDevice("home/livingroom/device/control", command); // Adjust topic as needed
        } else if ("bluetooth".equalsIgnoreCase(deviceType)) {
            // Bluetooth command handling already managed in startBluetoothServer
            System.out.println("Waiting for Bluetooth commands from connected clients.");
        }
    }
}

