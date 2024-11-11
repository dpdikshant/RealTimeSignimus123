
package com.signimusTask.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.signimusTask.entity.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
	
	Optional<Sensor> findByTypeAndLocation(String Type, String location);

	Optional<Sensor> findByType(String sensorType);
}


