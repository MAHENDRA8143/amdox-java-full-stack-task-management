package com.amdox.taskmanager.dto;

public class AuthResponse {

    private String email;
    private String role;
    private Long userId;
    private String token;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String email, String role, Long userId, String token, String message) {
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.token = token;
        this.message = message;
    }

    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }
    public String getToken() { return token; }
    public String getMessage() { return message; }

    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setToken(String token) { this.token = token; }
    public void setMessage(String message) { this.message = message; }
}
