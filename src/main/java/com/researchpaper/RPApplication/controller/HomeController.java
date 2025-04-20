package com.researchpaper.RPApplication.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.UserRepository;
import com.researchpaper.RPApplication.service.ResearchPaperService;
import com.researchpaper.RPApplication.service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ResearchPaperService researchPaperService;

    @Autowired
    public HomeController(UserService userService, 
                        UserRepository userRepository,
                        ResearchPaperService researchPaperService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.researchPaperService = researchPaperService;
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
    @PostMapping("/save-paper")
public ResponseEntity<Map<String, String>> savePaper(
        @RequestBody Map<String, Object> paper, 
        @SessionAttribute(value = "userId", required = false) String username) {
    
    // Extract fields from paper JSON data
    String title = (String) paper.get("title");
    String abstractText = (String) paper.get("abstractText");
    List<String> keywords = (List<String>) paper.get("keywords");
    
    // Safe extraction and casting of authors field
    List<Map<String, Object>> authors = null;
    if (paper.get("authors") instanceof List) {
        authors = (List<Map<String, Object>>) paper.get("authors");
    }
    List<Map<String, Object>> sections = (List<Map<String, Object>>) paper.get("sections");

    // Print details to console
    System.out.println("Received Paper Details:");
    System.out.println("Title: " + title);
    System.out.println("Abstract: " + abstractText);
    System.out.println("Keywords: " + keywords);
    System.out.println("Authors: ");
    
    if (authors != null) {
        for (Map<String, Object> author : authors) {
            System.out.println("Author Name: " + author.get("name"));
            System.out.println("Department: " + author.get("department"));
            System.out.println("Organization: " + author.get("organization"));
            System.out.println("City/Country: " + author.get("city"));
            System.out.println("Contact: " + author.get("contact"));
            System.out.println("----");
        }
    } else {
        System.out.println("No authors information provided.");
    }
    
    System.out.println("Sections:");
    if (sections != null) {
        for (Map<String, Object> section : sections) {
            System.out.println("Section Title: " + section.get("sectionTitle"));
            System.out.println("Section Content: " + section.get("content"));
            System.out.println("Position: " + section.get("position"));
            System.out.println("----");
        }
    }
    
    System.out.println("Username from session: " + username);
    
    if (username == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated. Please login first."));
    }
    
    try {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            System.out.println("User not found with username: " + username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }
        
        User user = userOptional.get();
        System.out.println("Saving paper for user: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        // Save the paper using the service
        researchPaperService.savePaper(paper, user.getId());
        
        System.out.println("Paper saved successfully!");
        return ResponseEntity.ok(Map.of("message", "Paper submitted successfully!"));
    } catch (Exception e) {
        System.out.println("Error saving paper: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to save paper: " + e.getMessage()));
    }
}
    
}
