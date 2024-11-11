package com.signimusTask.controller;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signimusTask.config.VoiceCommand;
import com.signimusTask.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
public class GoogleAssistantController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    // Webhook endpoint for Dialogflow
    @PostMapping("/webhook")
    public Map<String, Object> handleAssistantCommand(@RequestBody Map<String, Object> request) {
        try {
            JsonNode jsonRequest = objectMapper.convertValue(request, JsonNode.class);
            String intent = jsonRequest.at("/queryResult/intent/displayName").asText();
            String deviceType = jsonRequest.at("/queryResult/parameters/deviceType").asText();
            String location = jsonRequest.at("/queryResult/parameters/location").asText();
            int value = jsonRequest.at("/queryResult/parameters/value").asInt();

            VoiceCommand voiceCommand = new VoiceCommand();
            voiceCommand.setCommand(intent);
            voiceCommand.setDeviceType(deviceType);
            voiceCommand.setLocation(location);
            voiceCommand.setValue(value);

            String result = executeVoiceCommand(voiceCommand);
            return createResponse(result);
        } catch (Exception e) {
            return createResponse("I'm sorry, something went wrong.");
        }
    }

    private String executeVoiceCommand(VoiceCommand voiceCommand) {
        String command = voiceCommand.getCommand().toLowerCase();
        switch (command) {
            case "turn on":
                deviceService.executeAction("turn on " + voiceCommand.getDeviceType() + " in " + voiceCommand.getLocation());
                return "Turning on " + voiceCommand.getDeviceType();
            case "set temperature":
                deviceService.executeAction("set thermostat to " + voiceCommand.getValue() + " in " + voiceCommand.getLocation());
                return "Setting thermostat to " + voiceCommand.getValue();
            default:
                return "Unknown command";
        }
    }

    private Map<String, Object> createResponse(String text) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> fulfillment = new HashMap<>();
        fulfillment.put("fulfillmentText", text);
        response.put("fulfillmentResponse", fulfillment);
        return response;
    }
}
