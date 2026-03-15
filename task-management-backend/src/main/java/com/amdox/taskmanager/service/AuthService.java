package com.amdox.taskmanager.service;

import com.amdox.taskmanager.dto.AuthResponse;
import com.amdox.taskmanager.dto.LoginRequest;
import com.amdox.taskmanager.dto.RegisterRequest;
import com.amdox.taskmanager.model.User;
import com.amdox.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse(null, null, null, null, "Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setName(request.getName());

        User savedUser = userRepository.save(user);

        return new AuthResponse(
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getId(),
            null,
            "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new AuthResponse(null, null, null, null, "Invalid email or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(null, null, null, null, "Invalid email or password");
        }

        return new AuthResponse(
            user.getEmail(),
            user.getRole(),
            user.getId(),
            null,
            "Login successful"
        );
    }
}
