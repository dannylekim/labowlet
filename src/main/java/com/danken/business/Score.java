package com.danken.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Score {

    private Map<String, List<String>> roundScores;

    public Score() {
        roundScores = new HashMap<>();
    }

    public void setRoundScores(List<Round> rounds) {
        rounds.stream().map(Round::getRoundName).forEach(name -> roundScores.put(name, new ArrayList<>()));

    }

    public int getRoundScore(String roundName) {
        return roundScores.get(roundName).size();
    }

    public void addPoint(String roundName, String word) {
        roundScores.get(roundName).add(word);
    }

    public void removePoint(String roundName, String word) {
        roundScores.get(roundName).remove(word);
    }

    public Map<String, Integer> getScoreBoard() {
        Map<String, Integer> scoreboard = new HashMap<>();
        roundScores.forEach((k, v) -> scoreboard.put(k, v.size()));
        return scoreboard;
    }


}
