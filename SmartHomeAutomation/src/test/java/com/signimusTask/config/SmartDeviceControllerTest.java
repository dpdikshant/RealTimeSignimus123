
package com.signimusTask.config;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SmartDeviceControllerTest {

    @Mock
    private DeviceConnectivityService connectivityService;

    @InjectMocks
    private SmartDeviceController smartDeviceController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitializeConnections() {
        smartDeviceController.initializeConnections();

        // Verify that the connections are initialized
        verify(connectivityService, times(1)).startBluetoothServer();
        verify(connectivityService, times(1)).connectToWifiDevice();
    }

    @Test
    public void testControlDevice_Wifi() {
        String command = "ON";
        smartDeviceController.controlDevice("wifi", command);

        // Verify that the command was sent for a Wi-Fi device
        verify(connectivityService, times(1)).controlWifiDevice("home/livingroom/device/control", command);
    }

    @Test
    public void testControlDevice_Bluetooth() {
        String command = "OFF";
        smartDeviceController.controlDevice("bluetooth", command);

        // Verify that no Wi-Fi control is triggered when device is Bluetooth
        verify(connectivityService, never()).controlWifiDevice(anyString(), eq(command));
    }
}
