package com.danken.business;

import java.util.List;

import lombok.Data;

@Data
public class Scoreboard {


    private final List<TeamScore> scores;

}

@Data
class TeamScore {

    private final String teamId;

    private final String teamName;

    private final int previousScore;

    private final int totalScore;

}
