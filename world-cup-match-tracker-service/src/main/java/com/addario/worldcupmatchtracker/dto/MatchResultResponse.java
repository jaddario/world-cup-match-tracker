package com.addario.worldcupmatchtracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MatchResultResponse {

    private Long id;
    private String homeTeam;
    private String awayTeam;
    private Integer homeScore;
    private Integer awayScore;
    private LocalDate matchDate;
    private String location;
    private String stage;
}
