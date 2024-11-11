
package com.signimusTask.config;

import static org.mockito.Mockito.*;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

public class DeviceConnectivityServiceTest {

    @Mock
    private MqttClient mqttClient;

    @Mock
    private MessageChannel deviceControlOutputChannel;

    @InjectMocks
    private DeviceConnectivityService connectivityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConnectToWifiDevice() throws MqttException {
        // Mock behavior to simulate client not being connected initially
        when(mqttClient.isConnected()).thenReturn(false);

        connectivityService.connectToWifiDevice();
        
        verify(mqttClient, times(1)).connect();
    }

    @Test
    public void testControlWifiDevice() {
        String testMessage = "ON";
        connectivityService.controlWifiDevice("home/livingroom/device/control", testMessage);

        // Verify that the message is sent on the channel with the correct content
        verify(deviceControlOutputChannel, times(1)).send(new GenericMessage<>(testMessage));
    }
}
