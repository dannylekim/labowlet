package com.danken.business;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class Game {
    @Getter
    private List<String> wordBowl;
    @Getter
    private Map<Player, List<String>> wordsMadePerPlayer;
    @Getter
    private List<Round> rounds;
    @Getter
    private List<Team> teams;
    @Getter
    private boolean allowSkips;
    private int wordsPerPerson;
    @Getter
    private int currentRound;


    public Game(List<Team> teams, List<Round> rounds, RoomSettings roomSettings){
        this.teams = teams;
        this.rounds = rounds;
        this.wordsMadePerPlayer = new HashMap<>();
        this.wordBowl = new ArrayList<>();
        this.wordsPerPerson = roomSettings.getWordsPerPerson();
        this.allowSkips = roomSettings.isAllowSkips();
        this.currentRound = 1;
    }

    /***
     *  Creates a new word bowl with the input. Verifies that all the words are unique.
     *
     * @param inputWords This is a list of words that the user has made to be placed in the word bowl
     */

    public void addWordBowl(List<String> inputWords, Player player) {

        if (inputWords == null) {
            log.warn("Missing word entries, cannot input a null object");
            throw new IllegalArgumentException("Missing word entries! Cannot input a null object.");
        }

        if (inputWords.size() != wordsPerPerson) {
            log.warn("Missing word entries, you need to have {} entries", wordsPerPerson);
            throw new IllegalArgumentException("Missing word entries! You need to have " + wordsPerPerson + " entries!");
        }


        List<String> playerWordBowl = new ArrayList<>();

        inputWords.forEach(word -> {

            //checking for uniqueness
            if (playerWordBowl.contains(word)) {
                log.warn("Cannot have two of the same entries in the word bowl");
                throw new IllegalArgumentException("Cannot have two of the same entries in your word bowl!");
            }
            playerWordBowl.add(word);
        });

        log.info("Replacing the words inputted previously with the new ones");
        this.wordsMadePerPlayer.put(player, playerWordBowl);

    }

    public void prepareRounds(){
        if(wordsMadePerPlayer.size() != teams.size() * 2){
            throw new IllegalStateException("Rounds cannot be prepared until all words have been inputted by each player");
        }

        var allWords = wordsMadePerPlayer.values().stream().collect(ArrayList<String>::new, ArrayList::addAll, ArrayList::addAll);
        rounds.forEach(round -> round.setRemainingWords(allWords));
    }
}
