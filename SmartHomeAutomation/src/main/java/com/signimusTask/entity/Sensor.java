
package com.signimusTask.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sensor {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String type; // e.g., "motion", "temperature", "humidity"
 private String location; // e.g., "living room", "kitchen"

 private Double value; // e.g., temperature value or motion detection status (0 or 1)

 private Long lastUpdated; // Timestamp for the last update

 // Getters and Setters
 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }

 public String getType() { return type; }
 public void setType(String type) { this.type = type; }

 public String getLocation() { return location; }
 public void setLocation(String location) { this.location = location; }

 public Double getValue() { return value; }
 public void setValue(Double value) { this.value = value; }

 public Long getLastUpdated() { return lastUpdated; }
 public void setLastUpdated(Long lastUpdated) { this.lastUpdated = lastUpdated; }
}

