package com.signimusTask.errorHandling;

public class MqttConnectionException extends RuntimeException {
    public MqttConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
