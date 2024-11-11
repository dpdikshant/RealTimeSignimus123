
package com.signimusTask.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.signimusTask.config.VoiceCommand;
import com.signimusTask.entity.Device;
import com.signimusTask.service.DeviceService;

import java.util.Optional;

@RestController
@RequestMapping("/api/voice")
public class VoiceAssistantController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/command")
    public ResponseEntity<String> handleVoiceCommand(@RequestBody VoiceCommand command) {
        // Example: "Turn on the living room light"
        Optional<Device> device = deviceService.findDeviceByTypeAndLocation(command.getDeviceType(), command.getLocation());
        
        if (device.isPresent()) {
            String result;
            switch (command.getCommand().toLowerCase()) {
                case "turn on":
                    result = deviceService.updateDeviceStatus(device.get().getDeviceId(), "ON").getStatus();
                    break;
                case "turn off":
                    result = deviceService.updateDeviceStatus(device.get().getDeviceId(), "OFF").getStatus();
                    break;
                case "set temperature":
                    if (command.getDeviceType().equalsIgnoreCase("thermostat")) {
                        // Additional logic for thermostat
                        result = "Temperature set to " + command.getValue() + " degrees";
                        // Store this in the database or use it in your business logic as needed
                    } else {
                        return ResponseEntity.badRequest().body("Invalid device type for temperature setting.");
                    }
                    break;
                default:
                    return ResponseEntity.badRequest().body("Unsupported command.");
            }
            return ResponseEntity.ok("Command executed: " + result);
        }
        return ResponseEntity.badRequest().body("Device not found.");
    }
}
