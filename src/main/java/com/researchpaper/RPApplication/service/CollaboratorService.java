package com.researchpaper.RPApplication.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.researchpaper.RPApplication.model.Collaborator;
import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.User;
import com.researchpaper.RPApplication.repository.CollaboratorRepository;

@Service
public class CollaboratorService {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    // Method to add a collaborator to a paper
    public Collaborator addCollaborator(ResearchPaper paper, User user, Collaborator.Role role) {
        Collaborator collaborator = new Collaborator();
        collaborator.setPaper(paper);
        collaborator.setUser(user);
        collaborator.setRole(role);
        collaborator.setAddedAt(new Timestamp(System.currentTimeMillis()));
        return collaboratorRepository.save(collaborator);  // Save and return the collaborator
    }
}
