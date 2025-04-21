package com.researchpaper.RPApplication.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.researchpaper.RPApplication.model.Collaborator;
import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.AbstractRepository;
import com.researchpaper.RPApplication.repository.AuthorRepository;
import com.researchpaper.RPApplication.repository.KeywordRepository;
import com.researchpaper.RPApplication.repository.ResearchPaperRepository;
import com.researchpaper.RPApplication.repository.SectionRepository;
import com.researchpaper.RPApplication.repository.TemplateRepository;
import com.researchpaper.RPApplication.repository.UserRepository;
import com.researchpaper.RPApplication.service.CollaboratorService;
import com.researchpaper.RPApplication.service.ResearchPaperService;

import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/papers")
public class ResearchPaperController {

    @Autowired
    private ResearchPaperService researchPaperService;

    @Autowired
    private ResearchPaperRepository researchPaperRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private AbstractRepository abstractRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping("/create")
    public String showCreatePaperForm(Model model) {
        model.addAttribute("templates", templateRepository.findAll());
        return "create-paper";
    }
    
    

    @PostMapping("/create")
    public String createResearchPaper(@RequestParam("title") String title,
                                    @RequestParam("templateId") Long templateId,
                                    @RequestParam("collaborators[]") List<String> collaboratorEmails,
                                    @RequestParam("roles[]") List<String> roles,
                                    @SessionAttribute("userId") String username,
                                    HttpSession session,
                                    Model model) {

        model.addAttribute("templates", templateRepository.findAll());
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            model.addAttribute("errorMessage", "User not found for username: " + username);
            return "create-paper";
        }

        User user = userOptional.get();

        try {
            ResearchPaper paper = researchPaperService.createResearchPaper(title, templateId, user);
            session.setAttribute("paperId", paper.getId());
            System.out.println("!!!!!!!!!!!The Paper ID is: " + paper.getId());

            for (int i = 0; i < collaboratorEmails.size(); i++) {
                String collaboratorEmail = collaboratorEmails.get(i);
                if (collaboratorEmail.isEmpty()) {
                    continue;
                }
                Optional<User> collaboratorUserOpt = userRepository.findByEmail(collaboratorEmail);

                if (collaboratorUserOpt.isPresent()) {
                    collaboratorService.addCollaborator(paper, collaboratorUserOpt.get(), Collaborator.Role.valueOf(roles.get(i)));
                } else {
                    model.addAttribute("errorMessage", "User not found for email: " + collaboratorEmail);
                    return "create-paper";
                }
            }
            return "redirect:/papers/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while creating the paper: " + e.getMessage());
            return "create-paper";
        }
    }
}