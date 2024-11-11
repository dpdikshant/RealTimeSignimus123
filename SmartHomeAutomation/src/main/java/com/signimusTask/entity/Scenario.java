
package com.signimusTask.entity;

//Scenario.java

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Scenario {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;

 @ElementCollection
 private List<String> actions; // e.g., "turn on light in living room", "set thermostat to 22"

 private String conditionDescription; // SpEL condition, e.g., "#temperature > 20 and #timeOfDay == 'morning'"

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public List<String> getActions() {
	return actions;
}

public void setActions(List<String> actions) {
	this.actions = actions;
}

public String getConditionDescription() {
	return conditionDescription;
}

public void setConditionDescription(String conditionDescription) {
	this.conditionDescription = conditionDescription;
}



 
}
