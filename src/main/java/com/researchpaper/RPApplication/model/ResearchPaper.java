package com.researchpaper.RPApplication.model;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "research_papers")
public class ResearchPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToOne(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaperAbstract paperAbstract;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Author> authors;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords;

    // Constructors
    public ResearchPaper() {
    }

    public ResearchPaper(String title, User user, Template template) {
        this.title = title;
        this.user = user;
        this.template = template;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PaperAbstract getPaperAbstract() {
        return paperAbstract;
    }

    public void setPaperAbstract(PaperAbstract paperAbstract) {
        if (paperAbstract == null) {
            if (this.paperAbstract != null) {
                this.paperAbstract.setPaper(null);
            }
        } else {
            paperAbstract.setPaper(this);
        }
        this.paperAbstract = paperAbstract;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.setPaper(this);
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.setPaper(null);
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
        keyword.setPaper(this);
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void removeKeyword(Keyword keyword) {
        keywords.remove(keyword);
        keyword.setPaper(null);
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Convenience methods
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}