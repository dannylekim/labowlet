package business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
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
        benchPlayers.add(host);
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

    public boolean isInPlay() {
        return isInPlay;
    }

    /* public methods */

    /***
     * Create a team inside the group. You cannot have a team created if there's already the max number of teams.
     * Any player can create a group as long as they are in the same group. Cannot have the same name as another team.
     * It is assumed that the player creating the team will also join that team and leave their previous team.
     *
     * @param teamName
     * @param player
     */
    public void createTeam(String teamName, Player player) {
        //Cannot have more teams than the max teams
        if (teams.size() >= roomSettings.getMaxTeams()) {
            throw new IllegalStateException("Can no longer add any more teams in this room!");
        }

        //Must be in the room to create a team
        if (!isPlayerInRoom(player)) {
            throw new IllegalStateException("This player must be in the room to create a team.");
        }

        //cannot have two of the same names
        boolean doesTeamNameExist = teams.stream().anyMatch(team -> team.getTeamName().equals(teamName));
        if (doesTeamNameExist) {
            throw new IllegalArgumentException("This team name already exists in this room!");
        }

        //remove the player from previous team or bench
        removePlayer(player);

        Team newTeam = new Team(teamName, player);
        teams.add(newTeam);
    }

    public void addPlayerToBench(Player player) {
        benchPlayers.add(player);
    }

    /***
     * Add player to the team. It verifies if the player is in the room to begin with, then otherwise it will throw an
     * error. It will then momentarily remove the player from other teams or the bench and set the player into the
     * specified team.
     *
     * @param team
     * @param player
     * @return
     */
    public boolean addPlayerToTeam(Team team, Player player) {
        boolean hasPlayerJoinedTeam = false;
        boolean isPlayerInRoom = isPlayerInRoom(player);  //todo throw an error when false

        //todo verify that the team has an open slot to begin with. Also verify that this team doesn't have the player as well
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

    /***
     * Gets the first instance (and generally the ONLY instance) of the team with the specified ID.
     *
     * @param teamId
     * @return
     */
    public Team getTeam(String teamId) {
        Team foundTeam = teams.stream()
                .filter(team ->
                        team.getTeamId().equals(teamId))
                .findFirst()
                .orElse(null);
        return foundTeam;
    }

    /***
     *  This removes a player from the room and returns if the player has been removed.
     *
     * @param player
     * @return
     */
    public boolean removePlayer(Player player) {
        boolean isPlayerFound = benchPlayers.remove(player);
        if (!isPlayerFound) {

            //was not in bench therefore look in groups
            for(Team team: teams){
                isPlayerFound = team.removePlayerFromTeam(player);
                if(isPlayerFound){break;}
            }
        }
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

    /***
     * Generate a room code as long as the ROOM_CODE_LENGTH
     * //todo need to verify that the room Code isn't the same as any other room code
     *
     * @return
     */
    private String generateRoomCode() {
        StringBuilder token = new StringBuilder(ROOM_CODE_LENGTH);
        for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
            token.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        String generatedRoomCode = token.toString();
        return generatedRoomCode;
    }


    /***
     * Returns if the player is inside the room (either in the bench of the teams)
     *
     * @param player
     * @return
     */
    private boolean isPlayerInRoom(Player player) {
        boolean isPlayerInRoom = benchPlayers.contains(player);
        if (!isPlayerInRoom) {
            isPlayerInRoom = teams
                    .stream()
                    .anyMatch(team ->
                            (team.isPlayerInTeam(player)));
        }

        return isPlayerInRoom;
    }


}
