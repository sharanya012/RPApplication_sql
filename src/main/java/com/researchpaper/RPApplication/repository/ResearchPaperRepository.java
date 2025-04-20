// ResearchPaperRepository.java
package com.researchpaper.RPApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.researchpaper.RPApplication.model.ResearchPaper;
import com.researchpaper.RPApplication.model.User;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {
    List<ResearchPaper> findByUser(User user);
}