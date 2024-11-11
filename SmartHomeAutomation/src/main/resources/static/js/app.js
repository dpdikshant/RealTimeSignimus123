// Global AJAX configuration using jQuery
$.ajaxSetup({
    beforeSend: function(xhr) {
        const token = localStorage.getItem("authToken");
        if (token) {
            xhr.setRequestHeader("Authorization", "Bearer " + token);
        }
    },
    error: function(xhr) {
        if (xhr.status === 401) {
            showNotification("Unauthorized access. Please log in again.", "error");
            window.location.href = "/login";
        } else if (xhr.status === 500) {
            showNotification("Server error. Please try again later.", "error");
        }
    }
});

// Utility function for showing notifications
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show`;
    notification.role = 'alert';
    notification.innerHTML = `
        <strong>${type === 'error' ? 'Error!' : 'Info:'}</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
}

// Centralized error handler
function handleError(error) {
    console.error('An error occurred:', error);
    showNotification('An unexpected error occurred. Please try again later.', 'error');
}

// Event listeners
document.addEventListener("DOMContentLoaded", () => {
    console.log("App initialized");

    // Example function for API call with error handling
    function apiCallExample() {
        fetch('/api/some-endpoint')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                showNotification("Data loaded successfully!", "success");
            })
            .catch(error => handleError(error));
    }

    // Initialize notifications
    showNotification("Welcome to the Smart Home Dashboard", "info");
    apiCallExample(); // Call example function on load
});
