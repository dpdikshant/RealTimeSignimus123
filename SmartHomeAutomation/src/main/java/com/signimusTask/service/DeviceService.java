package com.signimusTask.service;



import com.signimusTask.entity.Device;
import com.signimusTask.repository.DeviceRepository;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    @Qualifier("mqttDeviceControlOutputChannel")
    private MessageChannel deviceControlOutputChannel;

    private MqttClient mqttClient;

    @PostConstruct
    public void init() {
        try {
            mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("MQTT connection lost: {}", cause.getMessage(), cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    logger.info("Message arrived from topic {}: {}", topic, new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.info("Message delivery complete.");
                }
            });
            mqttClient.connect();
            subscribeToDeviceDiscovery();
            logger.info("Connected to MQTT broker and subscribed to device discovery topic.");
        } catch (MqttException e) {
            logger.error("Failed to connect to MQTT broker: {}", e.getMessage(), e);
        }
    }

    public List<Device> getAllDevices() {
        try {
            logger.info("Retrieving all devices.");
            return deviceRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve devices: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving devices", e);
        }
    }

    public Optional<Device> getDeviceById(Long id) {
        try {
            logger.info("Retrieving device with ID: {}", id);
            return deviceRepository.findById(id);
        } catch (Exception e) {
            logger.error("Failed to retrieve device with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving device", e);
        }
    }

    public Device saveDevice(Device device) {
        try {
            logger.info("Saving device with ID: {}", device.getDeviceId());
            return deviceRepository.save(device);
        } catch (Exception e) {
            logger.error("Failed to save device with ID {}: {}", device.getDeviceId(), e.getMessage(), e);
            throw new RuntimeException("Error saving device", e);
        }
    }

    public Device updateDeviceStatus(String deviceId, String status) {
        try {
            logger.info("Updating status for device ID {} to {}", deviceId, status);
            Optional<Device> deviceOpt = deviceRepository.findByDeviceId(deviceId);
            if (deviceOpt.isPresent()) {
                Device device = deviceOpt.get();
                device.setStatus(status);
                return deviceRepository.save(device);
            } else {
                logger.warn("Device not found with ID: {}", deviceId);
                throw new RuntimeException("Device not found with ID: " + deviceId);
            }
        } catch (Exception e) {
            logger.error("Failed to update device status for ID {}: {}", deviceId, e.getMessage(), e);
            throw new RuntimeException("Error updating device status", e);
        }
    }

    private void subscribeToDeviceDiscovery() {
        try {
            mqttClient.subscribe("smartHome/deviceDiscovery", (topic, message) -> {
                String deviceInfo = new String(message.getPayload());
                String[] parts = deviceInfo.split(",");
                if (parts.length == 3) {
                    String deviceId = parts[0];
                    String deviceType = parts[1];
                    String location = parts[2];

                    Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceId);
                    if (existingDevice.isEmpty()) {
                        Device device = new Device();
                        device.setDeviceId(deviceId);
                        device.setDeviceType(deviceType);
                        device.setLocation(location);
                        device.setStatus("OFF");
                        deviceRepository.save(device);
                        logger.info("New device discovered and saved: {}", device);
                    }
                } else {
                    logger.warn("Invalid device info format received: {}", deviceInfo);
                }
            });
        } catch (MqttException e) {
            logger.error("Error subscribing to device discovery: {}", e.getMessage(), e);
        }
    }

    public Optional<Device> findDeviceByTypeAndLocation(String deviceType, String location) {
        try {
            logger.info("Finding device by type '{}' and location '{}'", deviceType, location);
            return deviceRepository.findByDeviceTypeAndLocation(deviceType, location);
        } catch (Exception e) {
            logger.error("Failed to find device by type {} and location {}: {}", deviceType, location, e.getMessage(), e);
            throw new RuntimeException("Error finding device", e);
        }
    }
    
    
    public void discoverDevices() {
        try {
            logger.info("Subscribing to device discovery topic 'smartHome/deviceDiscovery'.");
            
            mqttClient.subscribe("smartHome/deviceDiscovery", (topic, message) -> {
                String deviceInfo = new String(message.getPayload());
                logger.info("Received device discovery message: {}", deviceInfo);

                String[] parts = deviceInfo.split(",");
                if (parts.length == 3) {
                    String deviceId = parts[0];
                    String deviceType = parts[1];
                    String location = parts[2];

                    Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceId);
                    if (existingDevice.isEmpty()) {
                        Device device = new Device();
                        device.setDeviceId(deviceId);
                        device.setDeviceType(deviceType);
                        device.setLocation(location);
                        device.setStatus("OFF");
                        deviceRepository.save(device);
                        logger.info("New device discovered and saved: {}", device);
                    } else {
                        logger.info("Device with ID {} already exists, skipping save.", deviceId);
                    }
                } else {
                    logger.warn("Invalid device info format received: {}", deviceInfo);
                }
            });
        } catch (MqttException e) {
            logger.error("Error subscribing to device discovery: {}", e.getMessage(), e);
        }
    }


    public void executeAction(String action) {
        try {
            if (action.contains("turn on")) {
                logger.info("Turning on device with action: {}", action);
            } else if (action.contains("set thermostat")) {
                logger.info("Setting thermostat with action: {}", action);
            } else {
                logger.warn("Action not recognized: {}", action);
            }
        } catch (Exception e) {
            logger.error("Failed to execute action '{}': {}", action, e.getMessage(), e);
        }
    }

    public void sendDeviceCommand(String command) {
        try {
            deviceControlOutputChannel.send(MessageBuilder.withPayload(command).build());
            logger.info("Device command sent: {}", command);
        } catch (Exception e) {
            logger.error("Failed to send device command '{}': {}", command, e.getMessage(), e);
            throw new RuntimeException("Error sending device command", e);
        }
    }

    @PreDestroy
    public void disconnectMqttClient() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("MQTT client disconnected successfully.");
            }
        } catch (MqttException e) {
            logger.error("Failed to disconnect MQTT client: {}", e.getMessage(), e);
        }
    }

    public String executeVoiceCommand(String action) {
        try {
            logger.info("Executing voice command: {}", action);
            switch (action.toLowerCase()) {
                case "turn on":
                    return controlDevice("ON");
                case "turn off":
                    return controlDevice("OFF");
                case "status":
                    return getDeviceStatus();
                default:
                    logger.warn("Unknown device command received: {}", action);
                    return "Unknown device command";
            }
        } catch (Exception e) {
            logger.error("Failed to execute voice command '{}': {}", action, e.getMessage(), e);
            return "Error executing voice command";
        }
    }

    private String controlDevice(String command) {
        sendDeviceCommand(command);
        return "Device turned " + (command.equals("ON") ? "on" : "off");
    }

    private String getDeviceStatus() {
        Device device = findDeviceById("device_id");
        return device != null ? "Device status is " + device.getStatus() : "Device not found";
    }

    private Device findDeviceById(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId).orElse(null);
    }
}

