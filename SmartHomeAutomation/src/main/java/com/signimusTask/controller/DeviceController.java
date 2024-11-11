
package com.signimusTask.controller;

import com.signimusTask.entity.Device;
import com.signimusTask.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    // Get a list of all devices
    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    // Get details of a specific device by ID
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Optional<Device> device = deviceService.getDeviceById(id);
        return device.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Save a new device or update an existing one
    @PostMapping
    public Device saveDevice(@RequestBody Device device) {
        return deviceService.saveDevice(device);
    }

    // Update the status of a device by deviceId
    @PutMapping("/{deviceId}/status")
    public ResponseEntity<Device> updateDeviceStatus(@PathVariable String deviceId, @RequestBody String status) {
        try {
            Device updatedDevice = deviceService.updateDeviceStatus(deviceId, status);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Trigger device discovery via MQTT
    @PostMapping("/discover")
    public ResponseEntity<String> discoverDevices() {
        deviceService.discoverDevices();
        return ResponseEntity.ok("Device discovery triggered successfully");
    }

    // Send a command to a specific device (send to MQTT topic or channel)
    @PostMapping("/{deviceId}/command")
    public ResponseEntity<String> sendDeviceCommand(@PathVariable String deviceId, @RequestBody String command) {
        deviceService.sendDeviceCommand(command);  // Sends the command to the device
        return ResponseEntity.ok("Command sent to device: " + deviceId);
    }

    // Execute a custom action command (e.g., "turn on light in living room")
    @PostMapping("/action")
    public ResponseEntity<String> executeAction(@RequestBody String action) {
        deviceService.executeAction(action);
        return ResponseEntity.ok("Action executed: " + action);
    }
}

