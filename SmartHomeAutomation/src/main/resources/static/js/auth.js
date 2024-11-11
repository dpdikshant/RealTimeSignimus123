// auth.js

// User login
function login(username, password) {
    $.ajax({
        url: "/api/auth/login",
        type: "POST",
        data: JSON.stringify({ username, password }),
        contentType: "application/json",
        success: function(response) {
            localStorage.setItem("authToken", response.token);
            showNotification("Login successful!", "success");
            window.location.href = "/dashboard";
        },
        error: function(xhr) {
            showNotification("Login failed. Please check your credentials.", "danger");
        }
    });
}

// User registration
function register(username, password) {
    $.ajax({
        url: "/api/auth/register",
        type: "POST",
        data: JSON.stringify({ username, password }),
        contentType: "application/json",
        success: function(response) {
            showNotification("Registration successful!", "success");
            window.location.href = "/login";
        },
        error: function(xhr) {
            showNotification("Registration failed. Please try again.", "danger");
        }
    });
}

// Logout function
function logout() {
    localStorage.removeItem("authToken");
    showNotification("Logged out successfully.", "success");
    window.location.href = "/login";
}

// Event listeners for authentication forms
$("#loginForm").on("submit", function(e) {
    e.preventDefault();
    const username = $("#username").val();
    const password = $("#password").val();
    login(username, password);
});

$("#registerForm").on("submit", function(e) {
    e.preventDefault();
    const username = $("#username").val();
    const password = $("#password").val();
    register(username, password);
});

$("#logoutBtn").on("click", logout);
