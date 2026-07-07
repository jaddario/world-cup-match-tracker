package com.addario.worldcupmatchtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MatchResultRequest {

    @NotBlank
    private String homeTeam;

    @NotBlank
    private String awayTeam;

    @NotNull
    private Integer homeScore;

    @NotNull
    private Integer awayScore;

    @NotNull
    private LocalDate matchDate;

    @NotBlank
    private String location;

    @NotBlank
    private String stage;
}
