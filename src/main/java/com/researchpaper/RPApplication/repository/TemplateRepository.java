package com.researchpaper.RPApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.researchpaper.RPApplication.model.Template;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    // You can add custom queries here if needed
}
