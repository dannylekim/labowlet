package com.danken.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
public class Game {

    @JsonIgnore
    private List<String> wordBowl;

    @JsonIgnore
    private Map<Player, List<String>> wordsMadePerPlayer;

    private List<Round> rounds;

    private List<Team> teams;

    @JsonIgnore
    private int wordsPerPerson; // put this in the controller

    private int currentRound; //index

    private Player currentActor;

    private Player currentGuesser;

    @JsonIgnore
    private WordBowlInputState state;


    public Game(List<Team> teams, List<Round> rounds) {
        this.teams = teams;
        this.rounds = rounds;
        this.wordsMadePerPlayer = new HashMap<>();
        this.wordBowl = new ArrayList<>();
        this.currentRound = 1;

        List<Player> players = new ArrayList<>();
        teams.forEach(team ->
                players.addAll(team.getTeamMembers())
        );

        state = new WordBowlInputState(players);
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

        //todo move this check into the controller
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

        var userStatus = state.usersStatus.stream().filter(status -> status.getPlayer().equals(player)).findFirst().orElse(null);
        userStatus.setCompleted(true);
    }

    public void prepareRounds() {
        if (wordsMadePerPlayer.size() != teams.size() * 2) {
            throw new IllegalStateException("Rounds cannot be prepared until all words have been inputted by each player");
        }

        var allWords = wordsMadePerPlayer.values().stream().collect(ArrayList<String>::new, ArrayList::addAll, ArrayList::addAll);
        rounds.forEach(round -> round.setRemainingWords(allWords));
    }
}
