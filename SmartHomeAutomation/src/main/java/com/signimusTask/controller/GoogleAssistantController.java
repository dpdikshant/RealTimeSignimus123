package com.signimusTask.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signimusTask.config.VoiceCommand;
import com.signimusTask.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
public class GoogleAssistantController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Webhook endpoint for handling commands from Google Assistant via Dialogflow.
     * This method extracts the intent and parameters from the JSON request,
     * constructs a VoiceCommand object, and executes the appropriate action.
     *
     * @param request Map<String, Object> representing the JSON payload from Dialogflow.
     * @return A map containing the response to send back to Dialogflow.
     */
    @PostMapping("/webhook")
    public Map<String, Object> handleAssistantCommand(@RequestBody Map<String, Object> request) {
        try {
            // Convert the incoming request to a JSON tree for easy extraction of values
            JsonNode jsonRequest = objectMapper.convertValue(request, JsonNode.class);

            // Extract the intent name and relevant parameters
            String intent = jsonRequest.at("/queryResult/intent/displayName").asText();
            String deviceType = jsonRequest.at("/queryResult/parameters/deviceType").asText("");
            String location = jsonRequest.at("/queryResult/parameters/location").asText("");
            int value = jsonRequest.at("/queryResult/parameters/value").asInt(0);

            // Create a VoiceCommand object to store command details
            VoiceCommand voiceCommand = new VoiceCommand();
            voiceCommand.setCommand(intent);
            voiceCommand.setDeviceType(deviceType);
            voiceCommand.setLocation(location);
            voiceCommand.setValue(value);

            // Execute the command based on extracted information and generate a response
            String result = executeVoiceCommand(voiceCommand);
            return createResponse(result);

        } catch (Exception e) {
            // Handle any errors by sending a generic error message
            return createResponse("I'm sorry, something went wrong. Please try again.");
        }
    }

    /**
     * Executes the appropriate action based on the command provided.
     *
     * @param voiceCommand VoiceCommand object containing the command, device type, location, and value.
     * @return A string describing the outcome of the command.
     */
    private String executeVoiceCommand(VoiceCommand voiceCommand) {
        String command = voiceCommand.getCommand().toLowerCase();

        switch (command) {
            case "turn on":
                // Example command: "Turn on light in living room"
                deviceService.executeAction("turn on " + voiceCommand.getDeviceType() + " in " + voiceCommand.getLocation());
                return "Turning on " + voiceCommand.getDeviceType() + " in " + voiceCommand.getLocation();

            case "turn off":
                // Example command: "Turn off light in living room"
                deviceService.executeAction("turn off " + voiceCommand.getDeviceType() + " in " + voiceCommand.getLocation());
                return "Turning off " + voiceCommand.getDeviceType() + " in " + voiceCommand.getLocation();

            case "set temperature":
                // Example command: "Set thermostat to 22 in living room"
                deviceService.executeAction("set thermostat to " + voiceCommand.getValue() + " in " + voiceCommand.getLocation());
                return "Setting thermostat to " + voiceCommand.getValue() + " in " + voiceCommand.getLocation();

            default:
                // If the command isn't recognized, return a default response
                return "Sorry, I didn't understand the command.";
        }
    }

    /**
     * Creates a JSON response in the format expected by Dialogflow.
     *
     * @param text The text message to be sent back to the user via Google Assistant.
     * @return A Map formatted as a Dialogflow fulfillment response.
     */
    private Map<String, Object> createResponse(String text) {
        // Prepare the JSON response format for Dialogflow
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> fulfillment = new HashMap<>();
        fulfillment.put("fulfillmentText", text);
        response.put("fulfillmentResponse", fulfillment);
        return response;
    }
}

