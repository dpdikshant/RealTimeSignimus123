
package com.signimusTask.messageHandlers;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

public class DeviceStatusMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) {
        // Process the incoming device status here
        System.out.println("Received device status message: " + message.getPayload());
    }
}
