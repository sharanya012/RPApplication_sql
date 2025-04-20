// SectionRepository.java
package com.researchpaper.RPApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.researchpaper.RPApplication.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    void deleteAllByPaperId(Long paperId);
    List<Section> findByPaperIdOrderByPosition(Long paperId);
    List<Section> findByPaperIdOrderByPositionAsc(Long paperId);
}
