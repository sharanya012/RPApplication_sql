// PaperSubmissionDTO.java
package com.researchpaper.RPApplication.dto;

import java.util.List;

public class PaperSubmissionDTO {
    private String title;
    private List<AuthorDTO> authors;
    private String abstractText;
    private List<String> keywords;
    private List<SectionDTO> sections;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<AuthorDTO> getAuthors() { return authors; }
    public void setAuthors(List<AuthorDTO> authors) { this.authors = authors; }
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    public List<SectionDTO> getSections() { return sections; }
    public void setSections(List<SectionDTO> sections) { this.sections = sections; }
}