package business;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<Team> teams;
    private List<Player> players;
    private Player host;
    private String roomCode;
    private RoomSettings roomSettings;
    private List<String> wordBowl;
    private List<Round> rounds;

    public Room(Player host){
        teams = new ArrayList<>();
        players = new ArrayList<>();
        wordBowl = new ArrayList<>();
        rounds = new ArrayList<>();
        this.host = host;
        this.roomCode = generateRoomCode();
        roomSettings = new RoomSettings();
        //todo
    }

    /* only host methods */
    //todo could possibly not exist in this class and simply created as an api route that is fed into the business.Room constructor
    public void setRounds(int maxRounds){
        //todo
    }

    public void addRoundType(String roundType){
        //todo

    }

    public void deleteRoundType(String roundType){
        //todo

    }

    public void setMaxTeams(int maxTeams){
        //todo
    }

    public void setRoundTime(){
        //todo
    }

    public void setAllowSkips(boolean allowSkips){
        //todo
    }

    public void createRounds(){
        //todo
    }


    /* public methods */

    public void addTeam(Team team){
        teams.add(team);
        //todo verify if teams are there before adding
    }

    public void addPlayer(Player player){
        players.add(player);
        //todo
    }

    public void removePlayer(Player player){
        players.remove(player);
        //todo
    }

    public Score getScoreBoard(){
        return null;
        //todo, return a scoreboard, not a score
    }

    public void addWordToBowl(String word){
        wordBowl.add(word);
        //todo

    }

    private String generateRoomCode(){
        return null;
        //todo
    }


}
