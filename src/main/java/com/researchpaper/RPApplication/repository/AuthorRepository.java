// AuthorRepository.java
package com.researchpaper.RPApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.researchpaper.RPApplication.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByPaperIdOrderByPositionAsc(Long paperId);
    void deleteAllByPaperId(Long paperId);
}
