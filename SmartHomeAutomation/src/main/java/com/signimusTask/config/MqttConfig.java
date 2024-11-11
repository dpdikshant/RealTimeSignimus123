
package com.signimusTask.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.signimusTask.messageHandlers.DeviceStatusMessageHandler;
import com.signimusTask.messageHandlers.SensorMessageHandler;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.topic.sensors}")
    private String sensorTopic;

    @Value("${mqtt.topic.device-status}")
    private String deviceStatusTopic;

    @Bean
    public DefaultMqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        // Configure connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setKeepAliveInterval(30); // Set the keep-alive interval instead of connection lost timeout
        options.setAutomaticReconnect(true); // Automatically attempt reconnection

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(brokerUrl, clientId);
        
        // Set the callback on the MQTT client, not the MqttConnectOptions
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Message received: " + message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
					System.out.println("Delivery complete: " + token.getMessage());
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        client.connect();
        return client;
    }

    // Define input channels
    @Bean
    public MessageChannel sensorInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel deviceStatusInputChannel() {
        return new DirectChannel();
    }

    // Define message producers (inbound adapters for MQTT topics)
    @Bean
    public MessageProducer sensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
            brokerUrl, clientId + "_sensor", mqttClientFactory(), sensorTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);
        adapter.setOutputChannel(sensorInputChannel());
        return adapter;
    }

    @Bean
    public MessageProducer deviceStatusInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
            clientId + "_status", mqttClientFactory(), deviceStatusTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);
        adapter.setOutputChannel(deviceStatusInputChannel());
        return adapter;
    }

    // Define message handlers (subscribers for the input channels)
    @Bean
    @ServiceActivator(inputChannel = "sensorInputChannel")
    public MessageHandler sensorHandler() {
        return new SensorMessageHandler();  // Implement the handler to process sensor data
    }

    @Bean
    @ServiceActivator(inputChannel = "deviceStatusInputChannel")
    public MessageHandler deviceStatusHandler() {
        return new DeviceStatusMessageHandler();  // Implement the handler to process device status
    }

    // Define the outbound channel and handler (for device control)
    @Bean
    @ServiceActivator(inputChannel = "deviceControlOutputChannel")
    public MessageHandler deviceControlOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId + "_control", mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultTopic(deviceStatusTopic);  // or another appropriate topic for control
        return handler;
    }
    
    
    @Bean
    public MessageChannel deviceControlOutputChannel() {
        return new DirectChannel();
    }
    
    
    public String getBrokerUrl() {
        return brokerUrl;
    }

    // Getter for client ID
    public String getClientId() {
        return clientId;
    }

}








