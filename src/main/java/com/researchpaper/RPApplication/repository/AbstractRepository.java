// PaperAbstractRepository.java
package com.researchpaper.RPApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.researchpaper.RPApplication.model.PaperAbstract;


public interface AbstractRepository extends JpaRepository<PaperAbstract, Long> {
    PaperAbstract findByPaperId(Long paperId);
    void deleteByPaperId(Long paperId);
    
}
