package com.danken.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class Game {

    @JsonIgnore
    private List<String> wordBowl;

    @JsonIgnore
    private Map<Player, List<String>> wordsMadePerPlayer;

    @JsonIgnore
    private List<Round> rounds;

    private List<Team> teams;

    @JsonIgnore
    private int wordsPerPerson;

    @JsonIgnore
    private int currentRoundIndex;

    private Player currentActor;

    private Player currentGuesser;

    private Scoreboard currentScores;

    @JsonIgnore
    private int timeRemaining;

    @JsonIgnore
    int timeToCarryOver;

    @JsonIgnore
    private WordBowlInputState state;

    @JsonIgnore
    private boolean started;

    @JsonIgnore
    private boolean gameOver;


    Game(List<Team> teams, List<Round> rounds, int wordsPerPerson) {
        this.teams = teams;
        this.rounds = rounds;
        this.wordsMadePerPlayer = new HashMap<>();
        this.wordBowl = new ArrayList<>();
        this.currentRoundIndex = 0;

        List<Player> players = new ArrayList<>();
        teams.forEach(team ->
                players.addAll(team.getTeamMembers())
        );

        players.forEach(player -> wordsMadePerPlayer.put(player, new ArrayList<>()));

        this.wordsPerPerson = wordsPerPerson;
        state = new WordBowlInputState(players);
    }

    public Round getCurrentRound() {
        return rounds.get(currentRoundIndex);
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

        if (!wordsMadePerPlayer.containsKey(player)) {
            throw new IllegalStateException("A player without a team cannot add words!");
        }


        List<String> playerWordBowl = new ArrayList<>();


        inputWords.forEach(word -> {

            //checking for uniqueness
            if (playerWordBowl.contains(word)) {
                log.warn("Cannot have two of the same entries in the word bowl");
                throw new IllegalArgumentException("Cannot have two of the same entries in your word bowl! Please remove " + word);
            }
            playerWordBowl.add(word);
        });

        log.info("Replacing the words inputted previously with the new ones");
        this.wordsMadePerPlayer.put(player, playerWordBowl);

        var userStatus = state.usersStatus.stream().filter(status -> status.getPlayer().equals(player)).findFirst().orElseThrow();
        userStatus.setCompleted(true);
    }

    private void prepareRounds() {
        if (wordsMadePerPlayer.size() != teams.size() * 2) {
            throw new IllegalStateException("Rounds cannot be prepared until all words have been inputted by each player");
        }

        var allWords = wordsMadePerPlayer.values().stream().collect(ArrayList<String>::new, ArrayList::addAll, ArrayList::addAll);
        rounds.forEach(round -> round.setRemainingWords((List<String>) allWords.clone()));
    }

    public Scoreboard fetchScoreboard() {
        return new Scoreboard(this.teams.stream()
                .map(this::getTeamScore)
                .collect(Collectors.toList()));
    }

    private TeamScore getTeamScore(final Team team) {

        final Score teamScore = team.getTeamScore();
        int previousScore = 0;
        int currentTotal = 0;
        for (int i = 0; i < rounds.size(); i++) {
            final int roundScore = teamScore.getRoundScore(rounds.get(i).getRoundName());
            //assume that the scoreboard is always updated before this
            if (i != currentRoundIndex - 1) {
                previousScore += roundScore;
            }
            currentTotal += roundScore;
        }

        return new TeamScore(team.getTeamName(), previousScore, currentTotal);


    }


    public boolean startGame() {
        if (state.isReady()) {
            prepareRounds();
            setCurrentRoundActivePlayers();
            teams.stream().map(Team::getTeamScore).forEach(score -> score.setRoundScores(rounds));
            setStarted(true);
        }

        return started;
    }

    public void setCurrentRoundActivePlayers() {
        final var currentRoundTurn = rounds.stream().mapToInt(Round::getTurns).sum();
        final int currentPlayerIndex = (currentRoundTurn / teams.size()) % 2;
        final var currentTeam = teams.get(currentRoundTurn % teams.size());
        currentActor = currentTeam.getTeamMembers().get(currentPlayerIndex);
        currentGuesser = currentTeam.getTeamMembers().get(Math.abs(currentPlayerIndex - 1));
    }

    @JsonIgnore
    public Team getCurrentTeam() {
        final var currentRoundTurn = rounds.stream().mapToInt(Round::getTurns).sum();
        return teams.get(currentRoundTurn % teams.size());
    }


}
