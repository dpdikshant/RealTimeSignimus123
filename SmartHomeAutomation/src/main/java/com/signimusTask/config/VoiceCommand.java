
package com.signimusTask.config;



import lombok.Data;

@Data
public class VoiceCommand {
    private String command;        // e.g., "turn on", "set temperature"
    private String deviceType;     // e.g., "light", "thermostat"
    private String location;       // e.g., "living room"
    private int value;             // Optional: for commands like setting temperature
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
    
    
    
}


