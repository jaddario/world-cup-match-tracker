package com.addario.worldcupmatchtracker.repository;

import com.addario.worldcupmatchtracker.domain.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long>, JpaSpecificationExecutor<MatchResult> {
}
