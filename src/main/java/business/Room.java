package business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(Room.class);

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

        logger.info("Created a new room with " + host.getName() + " and " + host.getId());

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
        logger.info("Trying to create a team with  " + teamName + " and player " + player.getName() + " and ID " + player.getId());
        //Cannot have more teams than the max teams
        if (teams.size() >= roomSettings.getMaxTeams()) {
            logger.info("Trying to add a team when the room can no longer take any more. Max is:  " + roomSettings.getMaxTeams());
            throw new IllegalStateException("Can no longer add any more teams in this room!");
        }

        //Must be in the room to create a team
        if (!isPlayerInRoom(player)) {
            logger.info("This player does not belong in this room and therefore cannot create a team");

            throw new IllegalStateException("This player must be in the room to create a team.");
        }

        //cannot have two of the same names
        boolean doesTeamNameExist = teams.stream().anyMatch(team -> team.getTeamName().equals(teamName));
        if (doesTeamNameExist) {
            logger.info("This team name already exists, therefore cannot create this team");
            throw new IllegalArgumentException("This team name already exists in this room!");
        }

        //remove the player from previous team or bench
        removePlayer(player);
        logger.info("Team has been added");
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
        logger.info("Trying to add player " + player.getName() + " with ID " + player.getId() + " to " +
                "join " + team.getTeamId() + " with name " + team.getTeamName());

        if(team.getTeamMember1() != null && team.getTeamMember2() != null){
            logger.info("The team is full");
            throw new IllegalStateException("This team is already full!");
        }

        if(team.isPlayerInTeam(player)){
            logger.info("The team already has this player");
            throw new IllegalArgumentException("This player is already a part of this team!");
        }

        if (!isPlayerInRoom) {
            logger.info("The player is not even in this room");
            throw new IllegalStateException("Cannot add a player that's not joined in this room!");
        }

        boolean isPlayerRemoved = removePlayer(player);
        if (!isPlayerRemoved) {
            logger.error("The player can not be moved to another group for an unknown reason");
            throw new Exception("Player could not be moved to another group. Reason Unknown");
        }

        boolean hasPlayerBeenAdded = team.addPlayerInTeam(player);
        if(!hasPlayerBeenAdded){
            logger.error("The player can not be moved be added into the team for some reason");
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
        Team returnTeam = teams.stream()
                .filter(team ->
                        team.getTeamId().equals(teamId))
                .findFirst()
                .orElse(null);
        logger.debug("Returning the team " + ((returnTeam != null) ? returnTeam.getTeamName() : "null"));
        return returnTeam;
    }

    /***
     *  This removes a player from the room and returns if the player has been removed.
     *
     * @param player the player to remove from the room
     * @return a boolean if the removal was successful
     */
    public boolean removePlayer(Player player) {
        logger.info("Attempting to remove player " + player.getName() + " with ID " + player.getId());
        boolean isPlayerRemoved = benchPlayers.remove(player);
        if (!isPlayerRemoved) {

            logger.info("Player has not been removed, looking into the teams");
            //was not in bench therefore look in groups
            for(Team team: teams){
                isPlayerRemoved = team.removePlayerFromTeam(player);
                if(isPlayerRemoved){
                    logger.info("Player has been found inside the team. Removing the player now...");
                    if(team.getTeamMember1() == null || team.getTeamMember2() == null){
                        teams.remove(team);
                    }    
                    break;
                }
            }
        }

        logger.info("Has player been removed: " + isPlayerRemoved);
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

        logger.info("Player " + player.getName() + " with ID " + player.getId() + " is trying to input these words: " + ((inputWords != null) ? Arrays.toString(inputWords.toArray()) : "null"));

        if(!isPlayerInATeam(player)){
            logger.info("Player is not part a team, can not input words until then.");
            throw new IllegalStateException("This player is not part a team. You cannot input words until you have joined a team.");
        }

        if(!isLocked){
            logger.info("Cannot input words until the game has started.");
            throw new IllegalStateException("The host hasn't started the game yet! You can't input words until then.");
        }

        if(isInPlay){
            logger.info("Cannot input words, the game has started.");
            throw new IllegalStateException("The game has already started, cannot input words at this time!");
        }

        if(inputWords == null) {
            logger.info("Missing word entries, cannot input a null object");
            throw new IllegalArgumentException("Missing word entries! Cannot input a null object.");
        }

        if(inputWords.size() != roomSettings.getWordsPerPerson()){
            logger.info("Missing word entries, you need to have " + roomSettings.getWordsPerPerson() + " entries.");
            throw new IllegalArgumentException("Missing word entries! You need to have " + roomSettings.getWordsPerPerson() + " entries!");
        }


        List<String> playerWordBowl = new ArrayList<>();

        inputWords.stream().forEach(word -> {

            //checking for uniqueness
            if(playerWordBowl.contains(word)){
                logger.info("Cannot have two of the same entries in the word bowl");
                throw new IllegalArgumentException("Cannot have two of the same entries in your word bowl!");
            }
            playerWordBowl.add(word);
        });

        logger.info("Replacing the words inputted previously with the new ones");
        this.wordsMadePerPlayer.remove(player);
        this.wordsMadePerPlayer.put(player, playerWordBowl);

    }

    /***
     *
     * This method is to update a room with the new settings. Can not update if game is locked or already in play.
     * If the room settings changes max amount of teams and there are more teams than the max, it will bench the
     * last joined teams and then remove the teams associated.
     *
     *
     * @param roomSettings the new inputted room settings
     */
    public void updateRoom(RoomSettings roomSettings) {

        logger.info("Starting to update the room...");
        if(this.isInPlay || this.isLocked){
            logger.info("Cannot update the room, the game has been locked or already in play!");
            throw new IllegalStateException("Can not update the room, the game has been locked or already in play!");
        }

        this.roomSettings = roomSettings;



        //If there are more teams than the new updated Max Teams, you must bench the newly joined ones until
        //it is of equal sizing
        int maxTeams = roomSettings.getMaxTeams();
        int lastJoinedTeamIndex;
        Team teamToBench;
        logger.info("Removing the last joined members and teams if needed...");
        while (teams.size() > maxTeams) {
            logger.debug("Size of the team " + teams.size() + " is still bigger than max teams set by settings " + maxTeams);
            lastJoinedTeamIndex = teams.size() - 1;
            teamToBench = teams.get(lastJoinedTeamIndex);
            benchPlayers.add(teamToBench.getTeamMember1());
            benchPlayers.add(teamToBench.getTeamMember2());

            logger.debug("Benched " + teamToBench.getTeamMember1() + " and " + teamToBench.getTeamMember2());
            logger.debug("Removing team " + teamToBench.getTeamName() + " with ID " + teamToBench.getTeamId());
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

        String generatedRoomCode = token.toString();
        logger.info("Created room code " + generatedRoomCode);
        return generatedRoomCode;

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

        logger.info("Player " + player.getName() + " with ID " + player.getId() + " is inside the room: " + isPlayerInRoom);
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
        boolean isPlayerInATeam =  teams
                .stream()
                .anyMatch(team ->
                        (team.isPlayerInTeam(player)));

        logger.info("Player is in a team: " + isPlayerInATeam);
        return  isPlayerInATeam;
    }


}
