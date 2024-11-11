
package com.signimusTask.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.signimusTask.entity.Scenario;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

	Optional<Scenario> findByName(String scenarioName);
}

