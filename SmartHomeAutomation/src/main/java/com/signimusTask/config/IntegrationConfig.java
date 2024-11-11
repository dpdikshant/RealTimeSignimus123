
package com.signimusTask.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import com.signimusTask.messageHandlers.DeviceStatusMessageHandler;

@Configuration
public class IntegrationConfig {

    @Bean(name = "customDeviceControlOutputChannel")
    public MessageChannel deviceControlOutputChannel() {
        return new DirectChannel();
    }
    
  

    @Bean
    public DeviceStatusMessageHandler deviceStatusMessageHandler() {
        DeviceStatusMessageHandler handler = new DeviceStatusMessageHandler();
        deviceStatusInputChannel().subscribe(handler);
        return handler;
    }
    @Bean
    public SubscribableChannel deviceStatusInputChannel() {
        return new DirectChannel();
    }
    @Bean
    public void configureDeviceStatusHandler() {
        deviceStatusInputChannel().subscribe(deviceStatusMessageHandler());
    }

}
