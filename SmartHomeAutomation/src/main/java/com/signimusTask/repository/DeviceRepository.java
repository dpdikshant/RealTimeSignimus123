
package com.signimusTask.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.signimusTask.entity.Device;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);
    
    
    Optional<Device> findByDeviceTypeAndLocation(String deviceType, String location);
}
