// Toggle power on/off for a device
function toggleDevicePower(deviceId, status) {
    $.ajax({
        url: `/api/devices/${deviceId}/command`,
        type: "POST",
        data: JSON.stringify({ command: status }),
        contentType: "application/json",
        success: function(response) {
            showNotification(response.message, "success");
            loadDevices(); // Refresh device panel after toggling
        },
        error: function(xhr) {
            showNotification("Failed to update device status.", "danger");
        }
    });
}

// Set specific device settings (e.g., temperature)
function setDeviceSetting(deviceId, setting, value) {
    $.ajax({
        url: `/api/devices/${deviceId}/settings`,
        type: "PUT",
        data: JSON.stringify({ [setting]: value }),
        contentType: "application/json",
        success: function(response) {
            showNotification(`Device ${setting} set to ${value}`, "success");
        },
        error: function(xhr) {
            showNotification(`Failed to set ${setting} for device.`, "danger");
        }
    });
}

// Event listeners for device control buttons
$(document).on("click", ".toggle-power", function() {
    const deviceId = $(this).data("device-id");
    const currentStatus = $(this).data("status");
    const newStatus = currentStatus === "on" ? "off" : "on";
    toggleDevicePower(deviceId, newStatus);
});

$(document).on("change", ".device-setting", function() {
    const deviceId = $(this).data("device-id");
    const setting = $(this).data("setting");
    const value = $(this).val();
    setDeviceSetting(deviceId, setting, value);
});

// Fetch and render device control panel
function loadDevices() {
    fetch('/api/devices')
        .then(response => {
            if (!response.ok) throw new Error('Failed to load devices');
            return response.json();
        })
        .then(devices => renderDevicePanel(devices))
        .catch(error => showNotification('Failed to load devices.', 'danger'));
}

// Render the device control panel dynamically
function renderDevicePanel(devices) {
    const panel = document.getElementById('device-control-panel');
    panel.innerHTML = ''; // Clear existing content
    devices.forEach(device => {
        const deviceCard = document.createElement('div');
        deviceCard.className = 'col-12 col-md-4';
        deviceCard.innerHTML = `
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title">${device.name}</h5>
                    <button class="btn btn-primary toggle-power" data-device-id="${device.id}" data-status="${device.status}">
                        ${device.status === "on" ? "Turn Off" : "Turn On"}
                    </button>
                    <input type="range" class="form-range device-setting" data-device-id="${device.id}" data-setting="brightness" min="0" max="100" value="${device.brightness || 50}">
                </div>
            </div>
        `;
        panel.appendChild(deviceCard);
    });
}

// Initialize device panel on page load
document.addEventListener('DOMContentLoaded', loadDevices);
