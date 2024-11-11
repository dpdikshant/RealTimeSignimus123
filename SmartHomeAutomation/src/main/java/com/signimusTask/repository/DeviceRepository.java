
package com.signimusTask.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.signimusTask.entity.Device;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);
    
    
    Optional<Device> findByDeviceTypeAndLocation(String deviceType, String location);
}
