
package com.signimusTask.service;



import com.signimusTask.entity.Scenario;
import com.signimusTask.repository.ScenarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScenarioService {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioService.class);

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private DeviceService deviceService; // Assume DeviceService has methods to execute actions

    private final ExpressionParser parser = new SpelExpressionParser();

    public List<Scenario> getAllScenarios() {
        try {
            logger.info("Retrieving all scenarios.");
            return scenarioRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve scenarios: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving scenarios", e);
        }
    }

    public Scenario saveScenario(Scenario scenario) {
        try {
            logger.info("Saving scenario with name: {}", scenario.getName());
            return scenarioRepository.save(scenario);
        } catch (Exception e) {
            logger.error("Failed to save scenario with name {}: {}", scenario.getName(), e.getMessage(), e);
            throw new RuntimeException("Error saving scenario", e);
        }
    }

    public void executeScenario(Long scenarioId, Object contextData) {
        try {
            logger.info("Executing scenario with ID: {}", scenarioId);
            Scenario scenario = scenarioRepository.findById(scenarioId)
                    .orElseThrow(() -> new RuntimeException("Scenario not found with ID: " + scenarioId));

            if (evaluateCondition(scenario.getConditionDescription(), contextData)) {
                scenario.getActions().forEach(action -> {
                    logger.info("Executing action: {}", action);
                    deviceService.executeAction(action);
                });
            } else {
                logger.info("Condition not met for scenario ID: {}", scenarioId);
            }
        } catch (Exception e) {
            logger.error("Failed to execute scenario with ID {}: {}", scenarioId, e.getMessage(), e);
            throw new RuntimeException("Error executing scenario", e);
        }
    }

    private boolean evaluateCondition(String condition, Object contextData) {
        try {
            if (condition == null || condition.isEmpty()) {
                logger.info("No condition set for scenario, proceeding with execution.");
                return true;
            }

            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setRootObject(contextData);
            boolean result = parser.parseExpression(condition).getValue(context, Boolean.class);

            logger.info("Condition evaluated for scenario: {} - Result: {}", condition, result);
            return result;
        } catch (Exception e) {
            logger.error("Failed to evaluate condition '{}' with context data: {}", condition, e.getMessage(), e);
            throw new RuntimeException("Error evaluating scenario condition", e);
        }
    }

    public String executeScenarioVoiceCommand(String action) {
        try {
            logger.info("Executing voice command for scenario: {}", action);
            switch (action.toLowerCase()) {
                case "morning routine":
                    return executeScenarioByName("morning");
                case "evening routine":
                    return executeScenarioByName("evening");
                default:
                    logger.warn("Unknown scenario command received: {}", action);
                    return "Unknown scenario command";
            }
        } catch (Exception e) {
            logger.error("Failed to execute voice command for scenario '{}': {}", action, e.getMessage(), e);
            return "Error executing scenario voice command";
        }
    }

    private String executeScenarioByName(String scenarioName) {
        try {
            logger.info("Looking up scenario by name: {}", scenarioName);
            Scenario scenario = findScenarioByName(scenarioName);
            if (scenario != null) {
                executeScenario(scenario.getId(), null);
                logger.info("{} scenario executed successfully.", scenarioName);
                return scenarioName + " scenario started.";
            } else {
                logger.warn("Scenario not found with name: {}", scenarioName);
                return "Scenario not found";
            }
        } catch (Exception e) {
            logger.error("Failed to execute scenario by name '{}': {}", scenarioName, e.getMessage(), e);
            return "Error executing scenario by name";
        }
    }

    public Scenario findScenarioByName(String scenarioName) {
        try {
            logger.info("Searching for scenario with name: {}", scenarioName);
            return scenarioRepository.findByName(scenarioName).orElse(null);
        } catch (Exception e) {
            logger.error("Failed to find scenario with name '{}': {}", scenarioName, e.getMessage(), e);
            throw new RuntimeException("Error finding scenario by name", e);
        }
    }
}


