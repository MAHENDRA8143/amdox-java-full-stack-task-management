package com.amdox.taskmanager.controller;

import com.amdox.taskmanager.dto.AuthResponse;
import com.amdox.taskmanager.dto.LoginRequest;
import com.amdox.taskmanager.dto.RegisterRequest;
import com.amdox.taskmanager.security.JwtTokenProvider;
import com.amdox.taskmanager.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            if (isValidUserResponse(response)) {
                String token = jwtTokenProvider.generateToken(
                    response.getEmail(),
                    response.getRole(),
                    response.getUserId()
                );
                response.setToken(token);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            if (isValidUserResponse(response)) {
                String token = jwtTokenProvider.generateToken(
                    response.getEmail(),
                    response.getRole(),
                    response.getUserId()
                );
                response.setToken(token);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidUserResponse(AuthResponse response) {
        return response != null
                && response.getEmail() != null
                && !response.getEmail().isEmpty()
                && response.getRole() != null
                && !response.getRole().isEmpty()
                && response.getUserId() != null;
    }
}
