package com.researchpaper.RPApplication.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.researchpaper.RPApplication.model.Collaborator;
import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.TemplateRepository;
import com.researchpaper.RPApplication.repository.UserRepository;
import com.researchpaper.RPApplication.service.CollaboratorService;
import com.researchpaper.RPApplication.service.ResearchPaperService;

@Controller
@RequestMapping("/papers")
public class ResearchPaperController {

    @Autowired
    private ResearchPaperService researchPaperService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private UserRepository userRepository;  // Inject the UserRepository to fetch users

    @Autowired
    private CollaboratorService collaboratorService;  // Inject CollaboratorService to add collaborators

    // Display the form to create a new paper
    @GetMapping("/create")
    public String showCreatePaperForm(Model model) {
        model.addAttribute("templates", templateRepository.findAll());
        return "create-paper";  // This is the HTML template you have
    }

    @GetMapping("/view")
    public String showViewPaper() {
        return "write-paper";  // This is the HTML template you have
    }

    // Handle the form submission to create a new paper
    @PostMapping("/create")
    public String createResearchPaper(@RequestParam("title") String title,
                                      @RequestParam("templateId") Long templateId,
                                      @RequestParam("collaborators[]") List<String> collaboratorEmails,
                                      @RequestParam("roles[]") List<String> roles,
                                      @SessionAttribute("userId") String username, // Get username from session
                                      Model model) {

        // Add templates to model in case we need to return to form
        model.addAttribute("templates", templateRepository.findAll());

        // Fetch the user using username from session
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            model.addAttribute("errorMessage", "User not found for username: " + username);
            return "create-paper";
        }

        User user = userOptional.get();  // Get the User object from the Optional

        try {
            // Create the research paper using the service
            ResearchPaper paper = researchPaperService.createResearchPaper(title, templateId, user);

            // Add collaborators to the paper
            for (int i = 0; i < collaboratorEmails.size(); i++) {
                String collaboratorEmail = collaboratorEmails.get(i);
                if (collaboratorEmail.isEmpty()) {
                    System.out.println("⚠️ Skipping empty collaborator at index " + i);
                    continue;
                }
                Optional<User> collaboratorUserOpt = userRepository.findByEmail(collaboratorEmail);

                if (collaboratorUserOpt.isPresent()) {
                    Collaborator collaborator = collaboratorService.addCollaborator(paper, collaboratorUserOpt.get(), Collaborator.Role.valueOf(roles.get(i)));
                    System.out.println("✅ Collaborator added: " + collaboratorEmail);
                } else {
                    System.out.println("❌ Collaborator not found: " + collaboratorEmail);
                    model.addAttribute("errorMessage", "User not found for email: " + collaboratorEmail);
                    return "create-paper";
                }
                
                
            }
            System.out.println("--------------!!!!!!!!!!!!!!!!!Redirecting to /papers/view");
            return "redirect:/papers/view"; 
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while creating the paper: " + e.getMessage());
            System.out.println("---------------!!!!!!!!!!!!!!!Redirecting to /papers/create-paper");
            return "create-paper";
        }
    }
}
