
package com.signimusTask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.signimusTask.entity.Sensor;
import com.signimusTask.service.SensorService;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    // Retrieve all sensors
    @GetMapping
    public ResponseEntity<List<Sensor>> getAllSensors() {
        List<Sensor> sensors = sensorService.getAllSensors();
        return new ResponseEntity<>(sensors, HttpStatus.OK);
    }

    // Retrieve a specific sensor by type and location
    @GetMapping("/{type}/{location}")
    public ResponseEntity<Sensor> getSensorByTypeAndLocation(@PathVariable String type, @PathVariable String location) {
        return sensorService.getAllSensors().stream()
                .filter(sensor -> sensor.getType().equalsIgnoreCase(type) && sensor.getLocation().equalsIgnoreCase(location))
                .findFirst()
                .map(sensor -> new ResponseEntity<>(sensor, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Save or update a sensor
    @PostMapping
    public ResponseEntity<Sensor> saveSensor(@RequestBody Sensor sensor) {
        Sensor savedSensor = sensorService.saveSensor(sensor);
        return new ResponseEntity<>(savedSensor, HttpStatus.CREATED);
    }

    // Endpoint to receive and process sensor data from MQTT payloads
    @PostMapping("/data")
    public ResponseEntity<String> receiveSensorData(@RequestBody String payload) {
        try {
            sensorService.processSensorData(payload);
            return new ResponseEntity<>("Sensor data processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to process sensor data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for triggering device actions based on sensor readings (for testing purposes)
    @PostMapping("/trigger/{type}/{location}")
    public ResponseEntity<String> triggerAction(@PathVariable String type, @PathVariable String location, @RequestParam double value) {
        try {
            // Manually invoke the process method to trigger actions based on simulated sensor data
            String payload = String.format("{\"type\":\"%s\", \"location\":\"%s\", \"value\":%s}", type, location, value);
            sensorService.processSensorData(payload);
            return new ResponseEntity<>("Action triggered based on sensor data", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to trigger action", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
