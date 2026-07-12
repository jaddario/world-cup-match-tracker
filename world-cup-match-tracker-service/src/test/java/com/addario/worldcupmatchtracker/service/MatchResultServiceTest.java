package com.addario.worldcupmatchtracker.service;

import com.addario.worldcupmatchtracker.domain.MatchResult;
import com.addario.worldcupmatchtracker.dto.MatchResultRequest;
import com.addario.worldcupmatchtracker.repository.MatchResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class MatchResultServiceTest {

    @Autowired
    private MatchResultService service;

    @Autowired
    private MatchResultRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should return filtered and paged match results when search parameters are provided")
    void shouldReturnFilteredAndPagedMatchResults_whenSearchParametersAreProvided() {
        var argentinaMatch = createMatch("Argentina", "France", "Group", LocalDate.of(2026, 6, 14));
        var brazilMatch = createMatch("Brazil", "Germany", "Quarter-final", LocalDate.of(2026, 6, 20));
        repository.save(argentinaMatch);
        repository.save(brazilMatch);

        var result = service.search("ARG", null, "GROUP", 0, 10);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.getContent().getFirst().getHomeTeam()).isEqualTo("Argentina"),
                () -> assertThat(result.getContent().getFirst().getStage()).isEqualTo("Group")
        );
    }

    @Test
    @DisplayName("Should return a match result by id when it exists")
    void shouldFindMatchResultById_whenExistingIdProvided() {
        var savedMatch = repository.save(createMatch("Argentina", "France", "Group", LocalDate.of(2026, 6, 14)));

        var result = service.findById(savedMatch.getId());

        assertAll(
                () -> assertThat(result.getHomeTeam()).isEqualTo("Argentina"),
                () -> assertThat(result.getAwayTeam()).isEqualTo("France")
        );
    }

    @Test
    @DisplayName("Should throw an exception when searching for a non-existing match result")
    void shouldThrowIllegalArgumentException_whenMatchResultDoesNotExist() {
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Match result not found");
    }

    @Test
    @DisplayName("Should create a new match result when a valid request is provided")
    void shouldCreateMatchResult_whenValidRequestProvided() {
        var request = createRequest("Spain", "Italy", "Final", LocalDate.of(2026, 6, 18));

        var result = service.create(request);

        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getHomeTeam()).isEqualTo("Spain"),
                () -> assertThat(result.getAwayTeam()).isEqualTo("Italy"),
                () -> assertThat(repository.findById(result.getId())).isPresent()
        );
    }

    @Test
    @DisplayName("Should delete an existing match result")
    void shouldDeleteMatchResult_whenExistingIdProvided() {
        var savedMatch = repository.save(createMatch("Uruguay", "Portugal", "Round of 16", LocalDate.of(2026, 6, 24)));

        service.delete(savedMatch.getId());

        assertThat(repository.findById(savedMatch.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should throw an exception when deleting a non-existing match result")
    void shouldThrowIllegalArgumentException_whenDeletingNonExistingId() {
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Match result not found");
    }

    @Test
    @DisplayName("Should return all results when search filters are blank")
    void shouldReturnAllResults_whenSearchFiltersAreBlank() {
        repository.save(createMatch("Argentina", "France", "Group", LocalDate.of(2026, 6, 14)));
        repository.save(createMatch("Brazil", "Germany", "Quarter-final", LocalDate.of(2026, 6, 20)));

        var result = service.search("   ", null, " ", 0, 10);

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should return an empty page when no match matches the search criteria")
    void shouldReturnEmptyPage_whenNoMatchMatchesSearchCriteria() {
        repository.save(createMatch("Argentina", "France", "Group", LocalDate.of(2026, 6, 14)));

        var result = service.search("zzz", null, null, 0, 10);

        assertThat(result.getContent()).isEmpty();
    }

    private MatchResult createMatch(String homeTeam, String awayTeam, String stage, LocalDate matchDate) {
        var match = new MatchResult();
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setHomeScore(2);
        match.setAwayScore(1);
        match.setMatchDate(matchDate);
        match.setLocation("Test Venue");
        match.setStage(stage);
        return match;
    }

    private MatchResultRequest createRequest(String homeTeam, String awayTeam, String stage, LocalDate matchDate) {
        var request = new MatchResultRequest();
        request.setHomeTeam(homeTeam);
        request.setAwayTeam(awayTeam);
        request.setHomeScore(2);
        request.setAwayScore(1);
        request.setMatchDate(matchDate);
        request.setLocation("Test Venue");
        request.setStage(stage);
        return request;
    }
}
