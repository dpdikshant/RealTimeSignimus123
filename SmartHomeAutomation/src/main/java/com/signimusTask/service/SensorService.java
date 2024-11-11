

package com.signimusTask.service;



import com.signimusTask.config.SensorDataEvent;
import com.signimusTask.entity.Sensor;
import com.signimusTask.repository.SensorRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorService {

    private static final Logger logger = LoggerFactory.getLogger(SensorService.class);

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final double TEMPERATURE_THRESHOLD = 1.0;

    public List<Sensor> getAllSensors() {
        try {
            logger.info("Retrieving all sensors.");
            return sensorRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve sensors: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving sensors", e);
        }
    }

    public Sensor saveSensor(Sensor sensor) {
        try {
            logger.info("Saving sensor at location: {}", sensor.getLocation());
            return sensorRepository.save(sensor);
        } catch (Exception e) {
            logger.error("Failed to save sensor at location {}: {}", sensor.getLocation(), e.getMessage(), e);
            throw new RuntimeException("Error saving sensor", e);
        }
    }

    public void processSensorData(String payload) {
        try {
            logger.info("Processing sensor data payload: {}", payload);
            JSONObject json = new JSONObject(payload);
            String type = json.getString("type");
            String location = json.getString("location");
            double value = json.getDouble("value");

            Optional<Sensor> existingSensorOpt = sensorRepository.findByTypeAndLocation(type, location);
            Sensor sensor = existingSensorOpt.orElse(new Sensor());
            sensor.setType(type);
            sensor.setLocation(location);

            boolean isSignificantChange = existingSensorOpt.map(s -> Math.abs(s.getValue() - value) >= TEMPERATURE_THRESHOLD)
                                                           .orElse(true);

            if (isSignificantChange) {
                sensor.setValue(value);
                sensor.setLastUpdated(System.currentTimeMillis());
                sensorRepository.save(sensor);
                eventPublisher.publishEvent(new SensorDataEvent(this, sensor));
                logger.info("Sensor updated and event published for type: {}, location: {}, value: {}", type, location, value);

                handleSensorTriggeredActions(type, location, value);
            } else {
                logger.info("No significant change for sensor at location {} with type {}", location, type);
            }
        } catch (Exception e) {
            logger.error("Failed to process sensor data: {}", e.getMessage(), e);
        }
    }

    private void handleSensorTriggeredActions(String type, String location, double value) {
        try {
            switch (type.toLowerCase()) {
                case "motion":
                    if (value > 0) {
                        logger.info("Motion detected in location {}, triggering light on action.", location);
                        deviceService.executeAction("turn on light in " + location);
                    }
                    break;

                case "temperature":
                    if (value > 25) {
                        logger.info("High temperature detected in location {}, triggering cooling action.", location);
                        deviceService.executeAction("set thermostat in " + location + " to cool");
                    }
                    break;

                case "humidity":
                    if (value > 70) {
                        logger.info("High humidity detected in location {}, triggering dehumidifier action.", location);
                        deviceService.executeAction("turn on dehumidifier in " + location);
                    }
                    break;

                default:
                    logger.warn("No action defined for sensor type: {}", type);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error handling triggered action for sensor type '{}', location '{}', value '{}': {}", type, location, value, e.getMessage(), e);
        }
    }

    public String getSensorStatus(String sensorType) {
        try {
            logger.info("Getting status for sensor type: {}", sensorType);
            Sensor sensor = findSensorByType(sensorType);
            return sensor != null ? sensorType + " sensor status: " + sensor.getValue() : "Sensor not found";
        } catch (Exception e) {
            logger.error("Failed to get sensor status for type {}: {}", sensorType, e.getMessage(), e);
            return "Error retrieving sensor status";
        }
    }

    public Sensor findSensorByType(String sensorType) {
        try {
            logger.info("Searching for sensor by type: {}", sensorType);
            return sensorRepository.findByType(sensorType).orElse(null);
        } catch (Exception e) {
            logger.error("Failed to find sensor by type '{}': {}", sensorType, e.getMessage(), e);
            throw new RuntimeException("Error finding sensor by type", e);
        }
    }
}

