package com.danken.business;

import java.util.List;

import lombok.Data;

@Data
public class Scoreboard {


    private final List<TeamScore> scores;

}

@Data
class TeamScore {

    private final Team team;

    private final int previousScore;

    private final int totalScore;

}
