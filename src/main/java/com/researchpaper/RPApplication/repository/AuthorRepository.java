// AuthorRepository.java
package com.researchpaper.RPApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.researchpaper.RPApplication.model.Author;
import com.researchpaper.RPApplication.model.ResearchPaper;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByPaperIdOrderByPositionAsc(Long paperId);
    void deleteAllByPaperId(Long paperId);
    @Modifying
@Query("DELETE FROM Author a WHERE a.paper.id = :paperId")
void deleteAllByResearchPaperId(@Param("paperId") Long paperId);

    void deleteAllByPaper(ResearchPaper paper);
    void deleteByPaperId(Long paperId);


}
