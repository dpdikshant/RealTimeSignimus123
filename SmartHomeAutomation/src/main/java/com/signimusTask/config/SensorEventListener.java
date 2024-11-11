
package com.signimusTask.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.signimusTask.entity.Sensor;
import com.signimusTask.service.DeviceService;

@Component
public class SensorEventListener implements ApplicationListener<SensorDataEvent> {

 @Autowired
 private DeviceService deviceService;

 @Override
 public void onApplicationEvent(SensorDataEvent event) {
     Sensor sensor = event.getSensor();

     // Based on sensor type, perform actions
     if ("motion".equals(sensor.getType()) && sensor.getValue() > 0) {
         deviceService.executeAction("turn on light in " + sensor.getLocation());
     } else if ("motion".equals(sensor.getType()) && sensor.getValue() == 0) {
         deviceService.executeAction("turn off light in " + sensor.getLocation());
     }
 }
}

