
package com.signimusTask.config;


import org.springframework.context.ApplicationEvent;

import com.signimusTask.entity.Sensor;

public class SensorDataEvent extends ApplicationEvent {

 private final Sensor sensor;

 public SensorDataEvent(Object source, Sensor sensor) {
     super(source);
     this.sensor = sensor;
 }

 public Sensor getSensor() {
     return sensor;
 }
}


