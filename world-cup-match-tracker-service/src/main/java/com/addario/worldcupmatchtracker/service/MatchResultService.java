package com.addario.worldcupmatchtracker.service;

import com.addario.worldcupmatchtracker.domain.MatchResult;
import com.addario.worldcupmatchtracker.dto.MatchResultRequest;
import com.addario.worldcupmatchtracker.dto.MatchResultResponse;
import com.addario.worldcupmatchtracker.repository.MatchResultRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
import java.util.Locale;
import java.util.Optional;

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
            buildTeamPredicate(team, root, criteriaBuilder).ifPresent(predicates::add);
            buildDatePredicate(date, root, criteriaBuilder).ifPresent(predicates::add);
            buildStagePredicate(stage, root, criteriaBuilder).ifPresent(predicates::add);
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return repository.findAll(specification, PageRequest.of(page, size, Sort.by("matchDate").descending()))
                .map(this::toResponse);
    }

    private Optional<Predicate> buildTeamPredicate(String team, Root<MatchResult> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(team)
                .filter(value -> !value.isBlank())
                .map(this::normalizeSearchValue)
                .map(normalizedTeam -> criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("homeTeam")), "%" + normalizedTeam + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("awayTeam")), "%" + normalizedTeam + "%")
                ));
    }

    private Optional<Predicate> buildDatePredicate(LocalDate date, Root<MatchResult> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(date)
                .map(matchDate -> criteriaBuilder.equal(root.get("matchDate"), matchDate));
    }

    private Optional<Predicate> buildStagePredicate(String stage, Root<MatchResult> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(stage)
                .filter(value -> !value.isBlank())
                .map(this::normalizeSearchValue)
                .map(normalizedStage -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("stage")), normalizedStage));
    }

    private String normalizeSearchValue(String value) {
        return value.toLowerCase(Locale.ROOT).trim();
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
