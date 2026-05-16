package controller;

package com.movieapp.filmplatform.controller;

import com.movieapp.filmplatform.model.*;
import com.movieapp.filmplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Customer user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (IOException e) {
            model.addAttribute("error", "System error. Please try again.");
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.authenticate(email, password);
            if (user != null) {
                session.setAttribute("loggedInUser", user);
                return user.getRole().equals("admin") ? "redirect:/admin/users" : "redirect:/profile";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
        } catch (IOException e) {
            model.addAttribute("error", "System error");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("form", new ProfileUpdateForm()); // optional
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("form") ProfileUpdateForm form,
                                HttpSession session) throws IOException {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/login";

        currentUser.setName(form.getName());
        currentUser.setEmail(form.getEmail());

        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            currentUser.setPassword(form.getPassword());
        }

        userService.updateUser(currentUser);
        session.setAttribute("loggedInUser", currentUser);
        return "redirect:/profile?updated";
    }

    @GetMapping("/profile/delete")
    public String deleteAccount(HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        userService.deleteUser(user.getId());
        session.invalidate();
        return "redirect:/login?deleted";
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("loggedInUser", user);
        return "admin/user-list";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/login";
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
