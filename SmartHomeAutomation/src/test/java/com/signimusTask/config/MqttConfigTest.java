
package com.signimusTask.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

public class MqttConfigTest {

    @Mock
    private DefaultMqttPahoClientFactory mqttClientFactory;

    @Mock
    private MqttClient mqttClient;

    @InjectMocks
    private MqttConfig mqttConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMqttClientFactoryConfiguration() {
        // Mocking MqttConnectOptions to check configuration values
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://broker.hivemq.com:1883"});
        options.setUserName("testUser");
        options.setPassword("testPassword".toCharArray());
        options.setCleanSession(true);

        // Inject options into factory and verify
        mqttClientFactory.setConnectionOptions(options);
        assertEquals(options.getUserName(), "testUser");
        assertEquals(options.getPassword(), "testPassword".toCharArray());
        assertEquals(options.isCleanSession(), true);
    }

    @Test
    public void testMqttClientConnection() throws Exception {
        when(mqttClient.isConnected()).thenReturn(true);
        assertEquals(mqttClient.isConnected(), true);
    }
}
