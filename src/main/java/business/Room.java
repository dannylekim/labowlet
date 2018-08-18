package business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Room {

    // -------- DATA CONSTANTS ------------
    private List<Team> teams;
    private List<Player> benchPlayers;
    private Player host;
    private String roomCode;
    private RoomSettings roomSettings;
    private List<String> wordBowl;
    private List<Round> rounds;

    private boolean isInPlay;

    // ------- STATIC CONSTANTS --------------------- //
    private static final Random RANDOM = new Random();
    private static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";
    private static final int ROOM_CODE_LENGTH = 4;

    public Room(Player host, RoomSettings roomSettings) {
        teams = new ArrayList<>();
        benchPlayers = new ArrayList<>();
        wordBowl = new ArrayList<>();
        rounds = new ArrayList<>();
        this.host = host;
        this.roomCode = generateRoomCode();
        this.roomSettings = roomSettings;
        isInPlay = false;
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

    public RoomSettings getRoomSettings() {
        return roomSettings;
    }

    // ----------------------------------------------------------------------------------

    public boolean isInPlay(){
        return isInPlay;
    }

    /* public methods */

    public boolean createTeam(String teamName, Player player) {
        if (teams.size() < roomSettings.getMaxTeams()) {
            Team newTeam = new Team(teamName, player);
            teams.add(newTeam);
            return true;
        }
        return false;
    }

    public void addPlayerToBench(Player player) {
        benchPlayers.add(player);
    }

    public boolean addPlayerToTeam(Team team, Player player) {
        boolean hasPlayerJoinedTeam = false;
        boolean isPlayerInRoom = isPlayerInRoom(player);

        if (isPlayerInRoom) {
            boolean isPlayerRemoved = removePlayer(player);
            if (isPlayerRemoved) {
                if (team.getTeamMember1() == null) {
                    team.setTeamMember1(player);
                    hasPlayerJoinedTeam = true;
                } else if (team.getTeamMember2() == null) {
                    team.setTeamMember2(player);
                    hasPlayerJoinedTeam = true;
                }
            }
        }

        return hasPlayerJoinedTeam;

    }

    public boolean isPlayerInRoom(Player player) {
        boolean isPlayerInRoom = benchPlayers.contains(player);
        if (isPlayerInRoom) {
            return isPlayerInRoom;
        }

        isPlayerInRoom = teams
                .stream()
                .anyMatch(team ->
                        (team.getTeamMember1() == player || team.getTeamMember2() == player));


        return isPlayerInRoom;
    }

    public Team getTeam(String teamId) {
        Team foundTeam = teams.stream()
                .filter(team ->
                        team.getTeamId().equals(teamId))
                .findFirst()
                .orElse(null);
        return foundTeam;
    }

    public boolean removePlayer(Player player) {
        boolean isPlayerFound = benchPlayers.remove(player);
        if (isPlayerFound) {
            return isPlayerFound;
        } //found in bench players, no need to parse any further

        isPlayerFound = teams.removeIf(
                team ->
                        (team.getTeamMember2() == player || team.getTeamMember1() == player));

        return isPlayerFound;
    }

    public Score fetchScoreboard() {
        return null;
        //todo, return a scoreboard, not a score
    }

    public void addWordToBowl(String word) {
        wordBowl.add(word);
        //todo
    }

    public void updateRoom(RoomSettings roomSettings) {
        this.roomSettings = roomSettings;

        //If there are more teams than the new updated Max Teams, you must bench the newly joined ones until
        //it is of equal sizing
        int maxTeams = roomSettings.getMaxTeams();
        int lastJoinedTeamIndex;
        Team teamToBench;
        while (teams.size() > maxTeams) {
            lastJoinedTeamIndex = teams.size() - 1;
            teamToBench = teams.get(lastJoinedTeamIndex);
            benchPlayers.add(teamToBench.getTeamMember1());
            benchPlayers.add(teamToBench.getTeamMember2());
            teams.remove(teamToBench);
        }
    }

    //========== private methods ================/

    private String generateRoomCode() {
        StringBuilder token = new StringBuilder(ROOM_CODE_LENGTH);
        for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
            token.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        String generatedRoomCode = token.toString();
        return generatedRoomCode;
    }


}
