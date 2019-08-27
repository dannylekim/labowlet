package com.danken.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Score {

    private Map<String, List<String>> roundScores;

    Score() {
        roundScores = new HashMap<>();
    }

    void setRoundScores(List<Round> rounds) {
        rounds.stream().map(Round::getRoundName).forEach(name -> roundScores.put(name, new ArrayList<>()));

    }

    @JsonIgnore
    public int getRoundScore(String roundName) {
        return roundScores.get(roundName).size();
    }

    public void addPoint(String roundName, String word) {
        roundScores.get(roundName).add(word);
    }

    public int getTotalScore() {
        return roundScores.keySet().stream().mapToInt(this::getRoundScore).sum();
    }


}
