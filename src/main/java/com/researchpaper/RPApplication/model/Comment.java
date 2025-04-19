package com.researchpaper.RPApplication.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(name = "commented_at", updatable = false)
    private Timestamp commentedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(Timestamp commentedAt) {
        this.commentedAt = commentedAt;
    }
}
