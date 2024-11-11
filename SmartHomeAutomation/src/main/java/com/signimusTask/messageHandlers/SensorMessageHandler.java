
package com.signimusTask.messageHandlers;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

public class SensorMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) {
        // Process the incoming sensor data here
        System.out.println("Received sensor message: " + message.getPayload());
    }
}
