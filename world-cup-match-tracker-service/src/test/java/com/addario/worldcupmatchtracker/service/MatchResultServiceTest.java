package com.addario.worldcupmatchtracker.service;

import com.addario.worldcupmatchtracker.domain.MatchResult;
import com.addario.worldcupmatchtracker.repository.MatchResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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

        var result = service.search("arg", null, "group", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getHomeTeam()).isEqualTo("Argentina");
        assertThat(result.getContent().getFirst().getStage()).isEqualTo("Group");
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
}
