package com.addario.worldcupmatchtracker.repository;

import com.addario.worldcupmatchtracker.domain.MatchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MatchResultRepositoryTest {

    @Autowired
    private MatchResultRepository repository;

    @Test
    @DisplayName("Should save and load a match result when valid data is provided")
    void shouldSaveAndLoadAMatchResult_whenValidDataIsProvided() {
        var match = new MatchResult();
        match.setHomeTeam("Argentina");
        match.setAwayTeam("France");
        match.setHomeScore(3);
        match.setAwayScore(3);
        match.setMatchDate(LocalDate.of(2026, 6, 14));
        match.setLocation("Dallas");
        match.setStage("Group");

        var savedMatch = repository.save(match);
        var loadedMatch = repository.findById(savedMatch.getId());

        assertThat(loadedMatch).isPresent();
        assertThat(loadedMatch.get().getHomeTeam()).isEqualTo("Argentina");
        assertThat(loadedMatch.get().getAwayTeam()).isEqualTo("France");
    }
}
