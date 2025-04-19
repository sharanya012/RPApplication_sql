package com.researchpaper.RPApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.researchpaper.RPApplication.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    // Mapping for the Index page
    @GetMapping("/")
    public String index() {
        return "index"; // returns index.html (in templates folder)
    }

    // Mapping for the Login page
    @GetMapping("/userlogin")
    public String login() {
        return "login"; // returns login.html (in templates folder)
    }

    // Mapping for the Signup page
    @GetMapping("/signup")
    public String signup() {
        return "signup"; // returns signup.html (in templates folder)
    }

    // Signup endpoint
    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        try {
            String message = userService.registerUser(username, email, password);
            model.addAttribute("message", message);
            return "signup"; // return to signup page with message
        } catch (Exception e) {
            model.addAttribute("error", "Something went wrong during signup");
            return "signup"; // return with error message
        }
    }

    // Login endpoint
    @PostMapping("/userlogin")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            String message = userService.authenticateUser(username, password);
            model.addAttribute("message", message);
            session.setAttribute("userId", username);
            return "redirect:/"; // Redirect to a dashboard or user profile after login
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials, please try again.");
            return "login"; // return to login page with error message
        }
    }
    @GetMapping("/userlogout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/userlogin";
    }
}
