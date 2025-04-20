// KeywordRepository.java
package com.researchpaper.RPApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.researchpaper.RPApplication.model.Keyword;
import com.researchpaper.RPApplication.model.ResearchPaper;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByPaper(ResearchPaper paper);
    void deleteAllByPaperId(Long paperId);
    List<Keyword> findByPaperId(Long paperId);
}