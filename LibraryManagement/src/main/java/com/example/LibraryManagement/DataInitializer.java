package com.example.LibraryManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.LibraryManagement.model.User;
import com.example.LibraryManagement.repository.UserRepository;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if the admin user already exists
        Optional<User> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isEmpty()) {
            // If no admin user exists, create one
            User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@example.com", User.Role.ADMIN);
            userRepository.save(admin);
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}

