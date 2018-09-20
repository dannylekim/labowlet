package business;

import java.util.*;

public class Room {

    // -------- DATA CONSTANTS ------------
    private List<Team> teams;
    private List<Player> benchPlayers;
    private Player host;
    private String roomCode;
    private RoomSettings roomSettings;
    private List<String> wordBowl;
    private Map<Player, List<String>> wordsMadePerPlayer;
    private List<Round> rounds;

    //Game state -> fixme should this be refactored into its own object
    private boolean isLocked; //Locked in players and now must input words
    private boolean canStart; //All the words have now been inputted and all the players have readied up
    private boolean isInPlay; //Is the game in play


    // ------- STATIC CONSTANTS --------------------- //
    private static final Random RANDOM = new Random();
    private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ234567890";
    private static final int ROOM_CODE_LENGTH = 4;

    public Room(Player host, RoomSettings roomSettings) {
        teams = new ArrayList<>();
        benchPlayers = new ArrayList<>();
        benchPlayers.add(host);
        wordBowl = new ArrayList<>();
        wordsMadePerPlayer = new HashMap<>();
        rounds = new ArrayList<>();
        this.host = host;
        this.roomCode = generateRoomCode();
        this.roomSettings = roomSettings;

        //set State//
        isInPlay = false;
        canStart = false;
        isLocked = false;
    }

    //the below is simply used for @RequestBody when it occurs. They should NOT be used otherwise
    public Room(){}

    /***
     * DO NOT USE THIS METHOD. Only used for SPRING controllers and not for developers.
     *
     * @param roomCode
     */
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    // ----------------- GETTERS / SETTERS FOR PUBLIC JSON RETURN -------------------- //
    public String getRoomCode() {
        return roomCode;
    }

    public Player getHost() {
        return host;
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

    public Map<Player, List<String>> getWordsMadePerPlayer() {
        return wordsMadePerPlayer;
    }

    // ----------------------------------------------------------------------------------

    public boolean isInPlay() {
        return isInPlay;
    }

    public boolean canStart() {
        return canStart;
    }

    public boolean isLocked() {
        return isLocked;
    }

    // -----------------------------------------------------------------------------------

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
    public void addPlayerToTeam(Team team, Player player) throws Exception{
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

        boolean hasPlayerBeenAdded = team.addPlayerInTeam(player);
        if(!hasPlayerBeenAdded){
            throw new Exception("Player could not be added into the team. Reason Unknown.");
        }

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
                if(isPlayerRemoved){
                    if(team.getTeamMember1() == null || team.getTeamMember2() == null){
                        teams.remove(team);
                    }    
                    break;
                }
            }
        }
        return isPlayerRemoved;
    }

    public Score fetchScoreboard() {
        return null;
        //todo, return a scoreboard, not a score
    }


    public void setIsLocked(boolean isLocked){
        this.isLocked = isLocked;
    }


    /***
     *  Creates a new word bowl with the input. Verifies that all the words are unique.
     *
     * @param inputWords This is a list of words that the user has made to be placed in the word bowl
     */

    public void addWordBowl(List<String> inputWords, Player player){

        if(!isPlayerInATeam(player)){
            throw new IllegalStateException("This player is not part a team. You cannot input words until you have joined a team.");
        }

        if(!isLocked){
            throw new IllegalStateException("The host hasn't started the game yet! You can't input words until then.");
        }

        if(inputWords.size() != roomSettings.getWordsPerPerson()){
            throw new IllegalArgumentException("Missing word entries! You need to have " + roomSettings.getWordsPerPerson() + " entries!");
        }


        List<String> playerWordBowl = new ArrayList<>();

        inputWords.stream().forEach(word -> {

            //checking for uniqueness
            if(playerWordBowl.contains(word)){
                throw new IllegalArgumentException("Cannot have two of the same entries in your word bowl!");
            }
            playerWordBowl.add(word);
        });

        this.wordsMadePerPlayer.remove(player);
        this.wordsMadePerPlayer.put(player, playerWordBowl);

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
     * Returns true if the player is inside the room (either in the bench of the teams)
     *
     * @param player the player to verify if they are in the room
     * @return a boolean value if player is in the room or not
     */
    private boolean isPlayerInRoom(Player player) {
        boolean isPlayerInRoom = benchPlayers.contains(player);
        if (!isPlayerInRoom) {
            isPlayerInRoom = isPlayerInATeam(player);
        }

        return isPlayerInRoom;
    }

    /***
     *
     * Returns true if the player is inside a team.
     *
     * @param player the player to check all teams with
     * @return a boolean value if the player is in a team or not
     */
    private boolean isPlayerInATeam(Player player){
        return teams
                .stream()
                .anyMatch(team ->
                        (team.isPlayerInTeam(player)));
    }


}
