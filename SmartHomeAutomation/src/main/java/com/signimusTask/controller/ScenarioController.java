
package com.signimusTask.controller;

//ScenarioController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.signimusTask.entity.Scenario;
import com.signimusTask.service.ScenarioService;

import java.util.List;

@RestController
@RequestMapping("/api/scenarios")
public class ScenarioController {

 @Autowired
 private ScenarioService scenarioService;

 @GetMapping
 public ResponseEntity<List<Scenario>> getAllScenarios() {
     return ResponseEntity.ok(scenarioService.getAllScenarios());
 }

 @PostMapping
 public ResponseEntity<Scenario> createScenario(@RequestBody Scenario scenario) {
     return ResponseEntity.ok(scenarioService.saveScenario(scenario));
 }

 @PostMapping("/{id}/execute")
 public ResponseEntity<String> executeScenario(@PathVariable Long id, @RequestBody Object contextData) {
     scenarioService.executeScenario(id, contextData);
     return ResponseEntity.ok("Scenario executed successfully.");
 }
}


