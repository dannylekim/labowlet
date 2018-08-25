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
     * @param teamName the team name
     * @param player the player who is creating the team
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
     * @param team the team to place a player in
     * @param player the player who wants to join the team
     * @return a boolean value if the player has successfully joined or not
     */

    //todo should be a void method
    public boolean addPlayerToTeam(Team team, Player player) throws Exception{
        boolean isPlayerInRoom = isPlayerInRoom(player);

        if(team.getTeamMember1() != null && team.getTeamMember2() != null){
            throw new IllegalStateException("This team is already full!");
        }

        if(team.isPlayerInTeam(player)){
            throw new IllegalArgumentException("This player is already a part of this team!");
        }

        if (!isPlayerInRoom) {
            throw new IllegalStateException("Cannot add a player that's not joined in this room!");
        }

        boolean isPlayerRemoved = removePlayer(player);
        if (!isPlayerRemoved) {
            throw new Exception("Player could not be moved to another group. Reason Unknown");
        }

        return team.addPlayerInTeam(player);

    }

    /***
     * Gets the first instance (and generally the ONLY instance) of the team with the specified ID.
     *
     * @param teamId the unique ID of a team to find
     * @return returns the team found or null if the ID was invalid
     */
    public Team getTeam(String teamId) {
        return teams.stream()
                .filter(team ->
                        team.getTeamId().equals(teamId))
                .findFirst()
                .orElse(null);
    }

    /***
     *  This removes a player from the room and returns if the player has been removed.
     *
     * @param player the player to remove from the room
     * @return a boolean if the removal was successful
     */
    public boolean removePlayer(Player player) {
        boolean isPlayerRemoved = benchPlayers.remove(player);
        if (!isPlayerRemoved) {

            //was not in bench therefore look in groups
            for(Team team: teams){
                isPlayerRemoved = team.removePlayerFromTeam(player);
                if(isPlayerRemoved){break;}
            }
        }
        return isPlayerRemoved;
    }

    public Score fetchScoreboard() {
        return null;
        //todo, return a scoreboard, not a score
    }

    public void addWordsToBowl(List<String> words) {
        wordBowl.addAll(words);
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

    /***
     * Regenerates a new unique room code and sets it as this room's code.
     *
     * @return
     */
    public String regenerateRoomCode(){
        this.roomCode = generateRoomCode();
        return roomCode;
    }

    //========== private methods ================/

    /***
     * Generate a room code as long as the ROOM_CODE_LENGTH
     *
     * @return a unique code that is as long as the ROOM_CODE_LENGTH
     */
    private String generateRoomCode() {
        StringBuilder token = new StringBuilder(ROOM_CODE_LENGTH);
        for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
            token.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return token.toString();

    }


    /***
     * Returns if the player is inside the room (either in the bench of the teams)
     *
     * @param player the player to verify if they are in the room
     * @return a boolean value if player is in the room or not
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
