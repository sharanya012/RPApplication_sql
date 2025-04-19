package com.researchpaper.RPApplication.service;

import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.model.Template;
import com.researchpaper.RPApplication.repository.ResearchPaperRepository;
//import com.researchpaper.RPApplication.repository.CollaboratorRepository;
import com.researchpaper.RPApplication.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


@Service
public class ResearchPaperService {

    @Autowired
    private ResearchPaperRepository researchPaperRepository;

    @Autowired
    private TemplateRepository templateRepository;

    
    // Method to create a new research paper
    public ResearchPaper createResearchPaper(String title, Long templateId, User user) {
        Template template = templateRepository.findById(templateId).orElseThrow(() -> new RuntimeException("Template not found"));

        ResearchPaper paper = new ResearchPaper();
        paper.setTitle(title);
        paper.setUser(user);
        paper.setTemplate(template);
        paper.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        paper.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        ResearchPaper savedPaper = researchPaperRepository.save(paper);

        

        return savedPaper;
    }
}
