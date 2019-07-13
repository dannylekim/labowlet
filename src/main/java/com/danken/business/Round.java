package com.danken.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Round {

    private String roundName;

    @JsonIgnore
    private List<String> remainingWords;

    private int turns;

    private String wordToGuess;

    @JsonIgnore
    private Random randomNumber = new Random();

    Round(String roundName) {
        this.remainingWords = new ArrayList<>();
        this.turns = 0;
        this.roundName = roundName;

        log.info("Created a round with roundName " + this.roundName);

    }

    void setRemainingWords(List<String> words) {
        log.info("Setting these words {} into the {} round", Arrays.toString(words.toArray()), roundName);
        this.remainingWords = words;
    }

    public void getRandomWord() {
        int wordBowlSize = remainingWords.size();
        int randomIndex = randomNumber.nextInt(wordBowlSize - 1);
        this.wordToGuess =  remainingWords.get(randomIndex);
        log.info("Random word is " + wordToGuess);
    }

    public void removeWord(String word) {
        remainingWords.remove(word);
    }

    public void increaseTurnCounter() {
        turns++;
    }

}
