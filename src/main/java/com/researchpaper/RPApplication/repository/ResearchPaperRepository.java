// ResearchPaperRepository.java
package com.researchpaper.RPApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.User;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {
    List<ResearchPaper> findByUser(User user);
    @Query("SELECT rp.title FROM ResearchPaper rp WHERE rp.id = :paperId")
    String findTitleById(@Param("paperId") Long paperId);
    // New method to find papers by collaborator
    @Query("SELECT c.paper FROM Collaborator c WHERE c.user = :user")
    List<ResearchPaper> findByCollaborator(@Param("user") User user);
}