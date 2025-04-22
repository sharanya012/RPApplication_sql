package com.researchpaper.RPApplication.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.researchpaper.RPApplication.model.Author;
import com.researchpaper.RPApplication.model.Keyword;
import com.researchpaper.RPApplication.model.PaperAbstract;
import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.Section;
import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.AbstractRepository;
import com.researchpaper.RPApplication.repository.AuthorRepository;
import com.researchpaper.RPApplication.repository.KeywordRepository;
import com.researchpaper.RPApplication.repository.ResearchPaperRepository;
import com.researchpaper.RPApplication.repository.SectionRepository;
import com.researchpaper.RPApplication.repository.UserRepository;
import com.researchpaper.RPApplication.service.ResearchPaperService;
import com.researchpaper.RPApplication.service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ResearchPaperService researchPaperService;

    private final ResearchPaperRepository researchPaperRepository;
    private final AbstractRepository abstractRepository;
    private final AuthorRepository authorRepository;
    private final KeywordRepository keywordRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    public HomeController(UserService userService, 
                          UserRepository userRepository,
                          ResearchPaperService researchPaperService,
                          ResearchPaperRepository researchPaperRepository,
                          AbstractRepository abstractRepository,
                          AuthorRepository authorRepository,
                          KeywordRepository keywordRepository,
                          SectionRepository sectionRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.researchPaperService = researchPaperService;
        this.researchPaperRepository = researchPaperRepository;
        this.abstractRepository = abstractRepository;
        this.authorRepository = authorRepository;
        this.keywordRepository = keywordRepository;
        this.sectionRepository = sectionRepository;
    }


    // Mapping for the Index page
    @GetMapping("/")
    public String index() {
        return "index"; // returns index.html (in templates folder)
    }

    @GetMapping("/papersforview")
    public String papersforview() {
        return "view-papers"; // returns index.html (in templates folder)
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

@GetMapping("/paperdata")
@ResponseBody
public Map<String, Object> getPaperData(@SessionAttribute(value = "paperId", required = false) Long paperId) {
    Map<String, Object> response = new HashMap<>();

    if (paperId == null) {
        response.put("error", "No paperId found in session.");
        return response;
    }

    try {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        PaperAbstract paperAbstract = abstractRepository.findByPaperId(paperId);
        List<Author> authors = authorRepository.findByPaperIdOrderByPositionAsc(paperId);
        List<Keyword> keywords = keywordRepository.findByPaperId(paperId);
        List<Section> sections = sectionRepository.findByPaperIdOrderByPositionAsc(paperId);

        // ✅ Minimal paper info
        Map<String, Object> paperData = new HashMap<>();
        paperData.put("id", paper.getId());
        paperData.put("title", paper.getTitle());

        // ✅ Abstract
        Map<String, Object> abstractData = new HashMap<>();
        abstractData.put("text", paperAbstract != null ? paperAbstract.getContent() : "");

        // ✅ Authors
        List<Map<String, Object>> authorList = new ArrayList<>();
        for (Author author : authors) {
            Map<String, Object> authorMap = new HashMap<>();
            authorMap.put("name", author.getFullName());
            authorMap.put("department", author.getDepartment());
            authorMap.put("organization", author.getOrganization());
            authorMap.put("city", author.getCityCountry());
            authorMap.put("contact", author.getContact());
            authorMap.put("position", author.getPosition());
            authorList.add(authorMap);
        }

        // ✅ Keywords
        List<String> keywordList = keywords.stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.toList());

        // ✅ Sections
        List<Map<String, Object>> sectionList = new ArrayList<>();
        for (Section section : sections) {
            Map<String, Object> sectionMap = new HashMap<>();
            sectionMap.put("sectionTitle", section.getSectionTitle());
            sectionMap.put("content", section.getContent());
            sectionMap.put("position", section.getPosition());
            sectionList.add(sectionMap);
        }

        // ✅ Build final response
        response.put("paper", paperData);
        response.put("paperAbstract", abstractData);
        response.put("authors", authorList);
        response.put("keywords", keywordList);
        response.put("sections", sectionList);
        response.put("title", paper.getTitle());

        return response;

    } catch (Exception e) {
        response.put("error", "Error loading paper: " + e.getMessage());
        return response;
    }
}

@PostMapping("/update-paper")
public ResponseEntity<Map<String, String>> updatePaper(
        @RequestBody Map<String, Object> paper, 
        @SessionAttribute(value = "userId", required = false) String username,
        @SessionAttribute(value = "paperId", required = false) Long paperId) {
    
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
    System.out.println("Updating Paper Details:");
    System.out.println("Paper ID: " + paperId);
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
    
    if (paperId == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Paper ID not found in session."));
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
        System.out.println("Updating paper for user: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        // Update the paper using the service
        researchPaperService.updatePaper(paper, paperId, user.getId());
        
        System.out.println("Paper updated successfully!");
        return ResponseEntity.ok(Map.of("message", "Paper updated successfully!"));
    } catch (Exception e) {
        System.out.println("Error updating paper: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update paper: " + e.getMessage()));
    }
}

@PostMapping("/view-papers")
public ResponseEntity<List<Map<String, Object>>> viewPapers(
        @SessionAttribute(value = "userId", required = false) String username) {

    if (username == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    List<Map<String, Object>> paperSummaries = researchPaperService.getPaperSummariesByUsername(username);
    return ResponseEntity.ok(paperSummaries);
}


    @PostMapping("/set-paper-id")
public ResponseEntity<Void> setPaperIdInSession(@RequestBody Map<String, Long> requestBody,
                                                HttpSession session) {
    Long paperId = requestBody.get("paperId");
    if (paperId != null) {
        session.setAttribute("paperId", paperId);
        return ResponseEntity.ok().build();
    } else {
        return ResponseEntity.badRequest().build();
    }
}

@GetMapping("papers/view")
    public String viewPaper(@SessionAttribute("paperId") Long paperId, Model model) {
        try {
            System.out.println("\n===== START DEBUG OUTPUT =====");
            System.out.println("Paper ID from session: " + paperId);
            // Fetch the complete paper with all related data
            ResearchPaper paper = researchPaperRepository.findById(paperId)
                    .orElseThrow(() -> new RuntimeException("Paper not found"));
            System.out.println("here is the paper, im inside view!!!!!!!!");
            // Get all related data
            String title = researchPaperRepository.findTitleById(paperId);
        System.out.println("Paper Title (from dedicated query): " + title);
            PaperAbstract paperAbstract = abstractRepository.findByPaperId(paperId);
            List<Author> authors = authorRepository.findByPaperIdOrderByPositionAsc(paperId);
            List<Keyword> keywords = keywordRepository.findByPaperId(paperId);
            List<Section> sections = sectionRepository.findByPaperIdOrderByPositionAsc(paperId);
            //System.out.println("Full Paper Object: " + paper.toString());
            // Add all data to the model
            model.addAttribute("paper", paper);
            model.addAttribute("paperAbstract", paperAbstract);
            model.addAttribute("authors", authors);
            model.addAttribute("keywords", keywords);
            model.addAttribute("sections", sections);
            model.addAttribute("title", title); 

            return "write-paper-new";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading paper: " + e.getMessage());
            return "error";
        }
    }

}
