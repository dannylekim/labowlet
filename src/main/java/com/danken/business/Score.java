package com.danken.business;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Score {
    private Map<String, List<String>> roundScores;
    @Getter
    private int totalScore;

    public Score(){
        totalScore = 0;
        roundScores = new HashMap<>(); //todo initialize knowing the rounds
    }

    public int getRoundScore(String roundName){
        return 0;
    }

    public int getTotalScore(){
        return totalScore;
    }

    public void addPoint(String roundName, String word){
        //todo
    }

    public void removePoint(String roundName, String word){
        //todo
    }

    public Map getWordBoard() {
        return null;
        //todo
    }

    public Map getScoreBoard(){
        return null;

        //todo
    }


}
