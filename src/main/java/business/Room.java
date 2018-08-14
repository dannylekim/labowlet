package business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {

    // -------- DATA CONSTANTS ------------
    private List<Team> teams;
    private List<Player> benchPlayers;
    private Player host;
    private String roomCode;
    private RoomSettings roomSettings;
    private List<String> wordBowl;
    private List<Round> rounds;

    // ------- STATIC CONSTANTS --------------------- //
    private static final Random RANDOM = new Random();
    private static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";
    private static final int ROOM_CODE_LENGTH = 4;

    public Room(Player host, RoomSettings roomSettings){
        teams = new ArrayList<>();
        benchPlayers = new ArrayList<>();
        wordBowl = new ArrayList<>();
        rounds = new ArrayList<>();
        this.host = host;
        this.roomCode = generateRoomCode();
        this.roomSettings = roomSettings;
    }

    // ----------------- GETTERS / SETTERS FOR PUBLIC JSON RETURN -------------------- //
    public String getRoomCode() {
        return roomCode;
    }

    public List<Player> getBenchPlayers() {
        return benchPlayers;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public List<String> getWordBowl() {
        return wordBowl;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public RoomSettings getRoomSettings(){
        return roomSettings;
    }

    // ----------------------------------------------------------------------------------


    /* public methods */

    public void addTeam(Team team){
        teams.add(team);
        //todo verify if teams are there before adding
    }

    public void addPlayer(Player player){
        benchPlayers.add(player);
        //todo
    }

    public void removePlayer(Player player){
        //todo, find the player in the teams and bench players and remove that reference
    }

    public Score fetchScoreboard(){
        return null;
        //todo, return a scoreboard, not a score
    }

    public void addWordToBowl(String word){
        wordBowl.add(word);
        //todo
    }

    private String generateRoomCode(){
        StringBuilder token = new StringBuilder(ROOM_CODE_LENGTH);
        for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
            token.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        String generatedRoomCode = token.toString();
        return generatedRoomCode;
    }

    public void createTeam(){
        //todo
    }


}
