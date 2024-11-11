$(document).ready(function() {
    // Load all data when the page loads
    fetchDevices();
    fetchScenarios();
    fetchSensors();

    // Fetch and render devices
    function fetchDevices() {
        $.get("/api/devices", function(devices) {
            renderDevices(devices);
        });
    }

    // Fetch and render scenarios
    function fetchScenarios() {
        $.get("/api/scenarios", function(scenarios) {
            renderScenarios(scenarios);
        });
    }

    // Fetch and render sensors
    function fetchSensors() {
        $.get("/api/sensors", function(sensors) {
            renderSensors(sensors);
        });
    }

    // Render devices in the UI
    function renderDevices(devices) {
        let deviceHtml = '';
        devices.forEach(device => {
            deviceHtml += `<div class="device">
                <h3>${device.name}</h3>
                <div class="control-buttons">
                    <button class="btn ${device.status ? 'btn-danger' : 'btn-success'}" onclick="toggleDevice(${device.id}, ${device.status})">
                        ${device.status ? 'Turn Off' : 'Turn On'}
                    </button>
                    ${device.brightness !== undefined ? `
                        <div class="brightness-control">
                            <label for="brightness-${device.id}">Brightness</label>
                            <input type="range" id="brightness-${device.id}" min="0" max="100" value="${device.brightness}"
                                onchange="setBrightness(${device.id}, this.value)">
                        </div>
                    ` : ''}
                </div>
            </div>`;
        });
        $('#device-controls').html(deviceHtml);
    }

    // Render scenarios in the UI
    function renderScenarios(scenarios) {
        let scenarioHtml = '';
        scenarios.forEach(scenario => {
            scenarioHtml += `<div class="scenario">
                <h3>${scenario.name}</h3>
                <button class="btn btn-primary" onclick="activateScenario(${scenario.id})">Activate</button>
            </div>`;
        });
        $('#scenario-controls').html(scenarioHtml);
    }

    // Render sensors in the UI
    function renderSensors(sensors) {
        let sensorHtml = '';
        sensors.forEach(sensor => {
            sensorHtml += `<div class="sensor">
                <h3>${sensor.name}</h3>
                <p>Status: ${sensor.status}</p>
            </div>`;
        });
        $('#sensor-data').html(sensorHtml);
    }

    // Toggle device status
    window.toggleDevice = function(deviceId, currentStatus) {
        $.ajax({
            url: "/api/devices",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                deviceId: deviceId,
                status: !currentStatus
            }),
            success: function() {
                fetchDevices(); // Refresh device list
            },
            error: function(xhr) {
                alert("Failed to toggle device: " + xhr.responseText);
            }
        });
    };

    // Set device brightness
    window.setBrightness = function(deviceId, brightness) {
        $.ajax({
            url: "/api/devices",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                deviceId: deviceId,
                brightness: parseInt(brightness)
            }),
            success: function() {
                console.log("Brightness set successfully.");
            },
            error: function(xhr) {
                alert("Failed to set brightness: " + xhr.responseText);
            }
        });
    };

    // Activate a scenario
    window.activateScenario = function(scenarioId) {
        $.post(`/api/scenarios/${scenarioId}/activate`, function(response) {
            alert(response.message);
        });
    };

    // Send voice command to the backend
    window.sendVoiceCommand = function() {
        const command = $('#voiceCommand').val();
        $.post("/voice-assistant", { command: command }, function(response) {
            $('#voice-response').text(response.fulfillmentText);
        });
    };

    // Real-time updates using WebSocket
    const socket = new WebSocket("ws://localhost:8080/realtime");

    socket.onmessage = function(event) {
        const data = JSON.parse(event.data);
        updateDeviceUI(data);
    };

    function updateDeviceUI(device) {
        fetchDevices();
    }
});
