package com.researchpaper.RPApplication.service;

import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Signup: Register a new user
    public String registerUser(String username, String email, String password) {
        // Check if the username or email already exists
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists!";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already exists!";
        }

        // Create a new user and save to the database
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // In a real-world app, use encryption (bcrypt, etc.)
        
        userRepository.save(newUser);
        return "User registered successfully!";
    }

    // Login: Authenticate user
    public String authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return "Login successful!";
            } else {
                return "Incorrect password!";
            }
        } else {
            return "User not found!";
        }
    }
}
