package com.danken.business;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
public class Round {
    private String roundName;
    private List<String> remainingWords;
    private String description;
    private int turns;
    private Random randomNumber = new Random();
    public Round(List<String> roundWords, String description, String roundName){
        this.remainingWords = roundWords;
        this.description = description;
        this.turns = 0;
        this.roundName = roundName;

        log.info("Created a round with " + Arrays.toString(roundWords.toArray()) + " and roundName "
        + this.roundName + " with " + description);

    }

    public String getRandomWord(){
        int wordBowlSize = remainingWords.size();
        int randomIndex = randomNumber.nextInt(wordBowlSize - 1);
        String randomWord = remainingWords.get(randomIndex);
        log.info("Random word is " + randomWord);
        return randomWord;
    }

    public void removeWord(String word){
        remainingWords.remove(word);
    }

    public void increaseTurnCounter(){
        turns++;
    }

}
