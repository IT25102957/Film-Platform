package service;

package com.movieapp.filmplatform.service;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.util.FileHandler;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
public class UserService {
    private static final String FILE_PATH = "data/users.txt";
    private static final String SESSION_LOG_PATH = "data/sessions.txt";

    public void registerUser(User user) throws IOException {
        if (!user.validatePassword(user.getPassword())) {
            throw new IllegalArgumentException("Password does not meet requirements");
        }
        List<User> users = getAllUsers();
        int newId = users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
        user.setId(newId);
        user.setActive(true);

        String line = user.getId() + "|" + user.getName() + "|" + user.getEmail() + "|" +
                user.getPassword() + "|" + user.getRole() + "|" + user.isActive();
        FileHandler.appendLine(FILE_PATH, line);
    }

    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        List<String> lines = FileHandler.readLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\|");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            String email = parts[2];
            String password = parts[3];
            String role = parts[4];
            boolean active = Boolean.parseBoolean(parts[5]);

            User user = role.equals("admin") ?
                    new Admin(id, name, email, password, active) :
                    new Customer(id, name, email, password, active);
            users.add(user);
        }
        return users;
    }

    public User getUserById(int id) throws IOException {
        return getAllUsers().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    public User getUserByEmail(String email) throws IOException {
        return getAllUsers().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    public void updateUser(User updatedUser) throws IOException {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updatedUser.getId()) {
                users.set(i, updatedUser);
                break;
            }
        }
        saveAllUsers(users);
    }

    public void deleteUser(int id) throws IOException {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getId() == id);
        saveAllUsers(users);
    }

    private void saveAllUsers(List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            String line = u.getId() + "|" + u.getName() + "|" + u.getEmail() + "|" +
                    u.getPassword() + "|" + u.getRole() + "|" + u.isActive();
            lines.add(line);
        }
        FileHandler.writeLines(FILE_PATH, lines);
    }

    public User authenticate(String email, String password) throws IOException {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            logUserSession(user.getId());
            return user;
        }
        return null;
    }

    private void logUserSession(int userId) throws IOException {
        String logEntry = userId + "|" + java.time.LocalDateTime.now() + "|127.0.0.1";
        FileHandler.appendLine(SESSION_LOG_PATH, logEntry);
    }
}
