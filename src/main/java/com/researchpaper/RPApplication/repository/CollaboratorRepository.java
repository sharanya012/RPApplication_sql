package com.researchpaper.RPApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.researchpaper.RPApplication.model.Collaborator;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    // You can add custom queries here if needed
}
