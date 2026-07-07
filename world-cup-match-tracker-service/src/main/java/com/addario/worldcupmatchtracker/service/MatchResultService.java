package com.addario.worldcupmatchtracker.service;

import com.addario.worldcupmatchtracker.domain.MatchResult;
import com.addario.worldcupmatchtracker.dto.MatchResultRequest;
import com.addario.worldcupmatchtracker.dto.MatchResultResponse;
import com.addario.worldcupmatchtracker.repository.MatchResultRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchResultService {

    private final MatchResultRepository repository;

    @Transactional(readOnly = true)
    public MatchResultResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Match result not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<MatchResultResponse> search(String team, LocalDate date, String stage, int page, int size) {
        Specification<MatchResult> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (team != null && !team.isBlank()) {
                var pattern = "%" + team.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("homeTeam")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("awayTeam")), pattern))
                );
            }
            if (date != null) {
                predicates.add(criteriaBuilder.equal(root.get("matchDate"), date));
            }
            if (stage != null && !stage.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("stage")), stage.toLowerCase()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(specification, PageRequest.of(page, size, Sort.by("matchDate").descending()))
                .map(this::toResponse);
    }

    @Transactional
    public MatchResultResponse create(MatchResultRequest request) {
        var entity = new MatchResult();
        entity.setHomeTeam(request.getHomeTeam());
        entity.setAwayTeam(request.getAwayTeam());
        entity.setHomeScore(request.getHomeScore());
        entity.setAwayScore(request.getAwayScore());
        entity.setMatchDate(request.getMatchDate());
        entity.setLocation(request.getLocation());
        entity.setStage(request.getStage());

        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Match result not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private MatchResultResponse toResponse(MatchResult entity) {
        var response = new MatchResultResponse();
        response.setId(entity.getId());
        response.setHomeTeam(entity.getHomeTeam());
        response.setAwayTeam(entity.getAwayTeam());
        response.setHomeScore(entity.getHomeScore());
        response.setAwayScore(entity.getAwayScore());
        response.setMatchDate(entity.getMatchDate());
        response.setLocation(entity.getLocation());
        response.setStage(entity.getStage());
        return response;
    }
}
