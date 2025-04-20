package com.researchpaper.RPApplication.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.researchpaper.RPApplication.model.Author;
import com.researchpaper.RPApplication.model.Keyword;
import com.researchpaper.RPApplication.model.PaperAbstract;
import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.Section;
import com.researchpaper.RPApplication.model.Template;
import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.AbstractRepository;
import com.researchpaper.RPApplication.repository.AuthorRepository;
import com.researchpaper.RPApplication.repository.KeywordRepository;
import com.researchpaper.RPApplication.repository.ResearchPaperRepository;
import com.researchpaper.RPApplication.repository.SectionRepository;
import com.researchpaper.RPApplication.repository.TemplateRepository;
import com.researchpaper.RPApplication.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ResearchPaperService {

    @Autowired
    private ResearchPaperRepository researchPaperRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private AbstractRepository abstractRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;  // Added UserRepository


    // CREATE: Minimal Paper
    public ResearchPaper createResearchPaper(String title, Long templateId, User user) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        ResearchPaper paper = new ResearchPaper();
        paper.setTitle(title);
        paper.setUser(user);
        paper.setTemplate(template);
        paper.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        paper.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return researchPaperRepository.save(paper);
    }

    public ResearchPaper savePaper(Map<String, Object> paperData, Long userId) {
        // 1. Save the main paper
        ResearchPaper paper = saveResearchPaper(paperData, userId);
        
        // 2. Save abstract
        saveAbstract(paperData, paper);
        
        // 3. Save authors
        saveAuthors(paperData, paper);
        
        // 4. Save keywords
        saveKeywords(paperData, paper);
        
        // 5. Save sections
        saveSections(paperData, paper);
        
        return paper;
    }
    private ResearchPaper saveResearchPaper(Map<String, Object> paperData, Long userId) {
        ResearchPaper paper = new ResearchPaper();
        paper.setTitle((String) paperData.get("title"));
        paper.setUser(userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User not found")));
        
        // If you have template information in the paper data
        if (paperData.containsKey("templateId")) {
            int templateId = 1;
            // You would need to fetch the template and set it
            // paper.setTemplate(templateRepository.findById(templateId).orElse(null));
        }
        
        return researchPaperRepository.save(paper);
    }
    private void saveAbstract(Map<String, Object> paperData, ResearchPaper paper) {
        String abstractText = (String) paperData.get("abstractText");
        if (abstractText != null && !abstractText.isEmpty()) {
            PaperAbstract paperAbstract = new PaperAbstract();
            paperAbstract.setPaper(paper);
            paperAbstract.setContent(abstractText);
            abstractRepository.save(paperAbstract);
        }
    }
    
    private void saveAuthors(Map<String, Object> paperData, ResearchPaper paper) {
        List<Map<String, Object>> authors = (List<Map<String, Object>>) paperData.get("authors");
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                Map<String, Object> authorData = authors.get(i);
                Author author = new Author();
                author.setPaper(paper);
                author.setPosition(i);
                author.setFullName((String) authorData.get("name"));
                author.setDepartment((String) authorData.get("department"));
                author.setOrganization((String) authorData.get("organization"));
                author.setCityCountry((String) authorData.get("city"));
                author.setContact((String) authorData.get("contact"));
                
                authorRepository.save(author);
            }
        }
    }
    
    private void saveKeywords(Map<String, Object> paperData, ResearchPaper paper) {
        List<String> keywords = (List<String>) paperData.get("keywords");
        if (keywords != null) {
            for (String keyword : keywords) {
                Keyword keywordEntity = new Keyword();
                keywordEntity.setPaper(paper);
                keywordEntity.setKeyword(keyword);
                keywordRepository.save(keywordEntity);
            }
        }
    }
    
    private void saveSections(Map<String, Object> paperData, ResearchPaper paper) {
        List<Map<String, Object>> sections = (List<Map<String, Object>>) paperData.get("sections");
        if (sections != null) {
            for (Map<String, Object> sectionData : sections) {
                Section section = new Section();
                section.setPaper(paper);
                section.setSectionTitle((String) sectionData.get("sectionTitle"));
                section.setContent((String) sectionData.get("content"));
                section.setPosition(((Number) sectionData.get("position")).intValue());
                
                sectionRepository.save(section);
                
                // Save initial version
               
            }
        }
    }
    // SUBMIT: Complete Paper Submission
    @Transactional
    public ResearchPaper submitPaper(
            String title,
            String abstractText,
            List<String> keywords,
            List<Author> authors,
            List<Section> sections,
            User user
    ) {
        // Create the base paper
        ResearchPaper paper = new ResearchPaper();
        paper.setTitle(title);
        paper.setUser(user);
        paper.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        paper.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paper = researchPaperRepository.save(paper);

        // ABSTRACT
        PaperAbstract paperAbstract = new PaperAbstract();
        paperAbstract.setPaper(paper);
        paperAbstract.setContent(abstractText);
        abstractRepository.save(paperAbstract);

        // AUTHORS
        for (Author author : authors) {
            author.setPaper(paper);
            authorRepository.save(author);
        }

        // KEYWORDS
        for (String keywordStr : keywords) {
            Keyword keyword = new Keyword();
            keyword.setPaper(paper);
            keyword.setKeyword(keywordStr);
            keywordRepository.save(keyword);
        }

        // SECTIONS
        for (Section section : sections) {
            section.setPaper(paper);
            sectionRepository.save(section);
        }

        return paper;
    }

    // UPDATE: Existing Paper Details
    @Transactional
    public ResearchPaper updateResearchPaper(
            Long paperId,
            String title,
            String abstractText,
            List<String> keywords,
            List<Author> authors,
            List<Section> sections
    ) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research Paper not found"));

        // Update basic info
        paper.setTitle(title);
        paper.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // ABSTRACT
        PaperAbstract abs = abstractRepository.findByPaperId(paperId);
        if (abs == null) {
            abs = new PaperAbstract();
            abs.setPaper(paper);
        }
        abs.setContent(abstractText);
        abstractRepository.save(abs);

        // AUTHORS - Delete existing and add new
        authorRepository.deleteAllByPaperId(paperId);
        for (Author author : authors) {
            author.setPaper(paper);
            authorRepository.save(author);
        }

        // KEYWORDS - Delete existing and add new
        keywordRepository.deleteAllByPaperId(paperId);
        for (String keywordStr : keywords) {
            Keyword keyword = new Keyword();
            keyword.setPaper(paper);
            keyword.setKeyword(keywordStr);
            keywordRepository.save(keyword);
        }

        // SECTIONS - Delete existing and add new
        sectionRepository.deleteAllByPaperId(paperId);
        for (Section section : sections) {
            section.setPaper(paper);
            sectionRepository.save(section);
        }

        return researchPaperRepository.save(paper);
    }
}