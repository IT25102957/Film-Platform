package config;

package com.movieapp.filmplatform.config;

import com.movieapp.filmplatform.model.Admin;
import com.movieapp.filmplatform.model.User;
import com.movieapp.filmplatform.service.UserService;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Check if data directory exists
        if (!Files.exists(Paths.get("data"))) {
            Files.createDirectory(Paths.get("data"));
        }

        // Check if users.txt exists and has content
        String userFilePath = "data/users.txt";
        if (!Files.exists(Paths.get(userFilePath)) || Files.size(Paths.get(userFilePath)) == 0) {
            // Create default admin
            User admin = new Admin();
            admin.setName("System Admin");
            admin.setEmail("admin@film.com");
            admin.setPassword("Admin123");
            admin.setActive(true);

            try {
                userService.registerUser(admin);
                System.out.println("========================================");
                System.out.println("DEFAULT ADMIN CREATED!");
                System.out.println("Email: admin@film.com");
                System.out.println("Password: Admin123");
                System.out.println("========================================");
            } catch (IOException e) {
                System.err.println("Failed to create default admin: " + e.getMessage());
            }
        }
    }
}
