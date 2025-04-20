package com.researchpaper.RPApplication.model;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ResearchPaper paper;

    @Column(nullable = false)
    private Integer position; // For ordering authors

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String department;
    private String organization;

    @Column(name = "city_country")
    private String cityCountry;

    private String contact; // Can be email or ORCID

    // Constructors
    public Author() {}

    public Author(ResearchPaper paper, Integer position, String fullName, 
                 String department, String organization, 
                 String cityCountry, String contact) {
        this.paper = paper;
        this.position = position;
        this.fullName = fullName;
        this.department = department;
        this.organization = organization;
        this.cityCountry = cityCountry;
        this.contact = contact;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResearchPaper getPaper() {
        return paper;
    }

    public void setPaper(ResearchPaper paper) {
        this.paper = paper;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCityCountry() {
        return cityCountry;
    }

    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}