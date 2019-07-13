package com.danken.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Slf4j
public class Room {

    // -------- DATA MEMBERS ------------
    private List<Team> teams;

    private List<Player> benchPlayers;

    private Player host;

    private String roomCode; //given a setter to use for @RequestBody

    private RoomSettings roomSettings;

    @JsonIgnore
    @Setter
    private Game game;

    // ------- STATIC CONSTANTS --------------------- //
    private static final Random RANDOM = new Random();

    private static final String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ234567890";

    private static final int ROOM_CODE_LENGTH = 4;

    public Room(Player host, RoomSettings roomSettings) {
        teams = new ArrayList<>();
        benchPlayers = new ArrayList<>();
        benchPlayers.add(host);
        this.host = host;
        this.roomCode = generateRoomCode();
        this.roomSettings = roomSettings;

        log.info("Created a new room with {} and {}", host.getName(), host.getId());

        //set State// //fixme move this into GameState

        createEmptyTeams(roomSettings.getMaxTeams());

    }

    public Game createGame() {
        if (!isCanStart()) {
            throw new IllegalStateException("Game cannot start.");
        }

        var rounds = getRoomSettings().getRoundTypes().stream().map(Round::new).collect(Collectors.toList());
        var teams = getTeams();

        game = new Game(teams, rounds);

        return this.game;
    }

    public boolean isCanStart() {
        int teamsFilled = 0;
        for (Team team : teams) {
            List<Player> teamMembers = team.getTeamMembers();
            if (teamMembers.size() == Team.MAX_TEAM_MEMBERS) {
                teamsFilled++;
                //if a team is not filled or empty, then it is missing players and cannot start
            } else if (teamMembers.size() != 0) {
                return false;
            }
        }

        //can't play with just one team
        return teamsFilled > 1;
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
        log.info("Trying to create a team with  " + teamName + " and player " + player.getName() + " and ID " + player.getId());
        //Cannot have more teams than the max teams

        var emptyTeam = teams.stream().filter(Team::isEmpty).findFirst().orElse(null);

        if (emptyTeam == null) {
            log.warn("Trying to add a team when the room can no longer take any more. Max is:  {}", roomSettings.getMaxTeams());
            throw new IllegalStateException("Can no longer add any more teams in this room!");
        }

        //Must be in the room to create a team
        if (!isPlayerInRoom(player)) {
            log.warn("This player does not belong in this room and therefore cannot create a team");

            throw new IllegalStateException("This player must be in the room to create a team.");
        }

        //cannot have two of the same names
        boolean doesTeamNameExist = teams.stream().anyMatch(team -> team.getTeamName().equals(teamName));
        if (doesTeamNameExist) {
            log.warn("This team name already exists, therefore cannot create this team");
            throw new IllegalArgumentException("This team name already exists in this room!");
        }

        //remove the player from previous team or bench
        removePlayer(player);
        log.info("Team has been added");
        teams.remove(emptyTeam);
        var newTeam = new Team(teamName);
        newTeam.addPlayerInTeam(player);
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
    public void addPlayerToTeam(Team team, Player player) throws Exception {
        boolean isPlayerInRoom = isPlayerInRoom(player);
        log.info("Trying to add player {} with ID {} to join {} with name {}", player.getName(), player.getId(), team.getTeamId(), team.getTeamName());

        if (team.getTeamMembers().size() >= 2) {
            log.warn("The team is full");
            throw new IllegalStateException("This team is already full!");
        }

        if (team.isPlayerInTeam(player)) {
            log.warn("The team already has this player");
            throw new IllegalArgumentException("This player is already a part of this team!");
        }

        if (!isPlayerInRoom) {
            log.warn("The player is not even in this room");
            throw new IllegalStateException("Cannot add a player that's not joined in this room!");
        }

        boolean isPlayerRemoved = removePlayer(player);
        if (!isPlayerRemoved) {
            log.error("The player can not be moved to another group for an unknown reason");
            throw new Exception("Player could not be moved to another group. Reason Unknown");
        }

        boolean hasPlayerBeenAdded = team.addPlayerInTeam(player);
        if (!hasPlayerBeenAdded) {
            log.error("The player can not be moved be added into the team for some reason");
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
        log.debug("Returning the team {}", ((returnTeam != null) ? returnTeam.getTeamName() : "null"));
        return returnTeam;
    }

    /***
     *  This removes a player from the room and returns if the player has been removed.
     *
     * @param player the player to remove from the room
     * @return a boolean if the removal was successful
     */
    public boolean removePlayer(Player player) {
        log.info("Attempting to remove player {} with ID {}", player.getName(), player.getId());
        boolean isPlayerRemoved = benchPlayers.remove(player);
        if (!isPlayerRemoved) {

            log.info("Player has not been removed, looking into the teams");
            //was not in bench therefore look in groups
            for (Team team : teams) {
                isPlayerRemoved = team.removePlayerFromTeam(player);
                if (isPlayerRemoved) {
                    log.info("Player has been found inside the team. Removing the player now...");
                    break;
                }
            }
        }

        log.info("Has player been removed: {}", isPlayerRemoved);
        return isPlayerRemoved;
    }

    public Score fetchScoreboard() {
        return null;
        //todo, return a scoreboard, not a score
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

        log.info("Starting to update the room...");

        this.roomSettings = roomSettings;


        //If there are more teams than the new updated Max Teams, you must bench the newly joined ones until
        //it is of equal sizing
        int maxTeams = roomSettings.getMaxTeams();
        log.info("Removing the last joined members and teams if needed...");

        if (maxTeams > teams.size()) {
            createEmptyTeams(maxTeams - teams.size());
        }

        removeLastTeams(maxTeams);
    }

    private void removeLastTeams(int maxTeams) {

        Team teamToBench;
        while (teams.size() > maxTeams) {

            log.info("Checking for empty teams");
            var emptyTeams = teams.stream().filter(Team::isEmpty).collect(Collectors.toList());

            if (emptyTeams.size() > 0) {
                teamToBench = emptyTeams.get(0);
            } else {
                int lastJoinedTeamIndex;
                log.debug("Size of the team {} is still bigger than max teams set by settings {}", teams.size(), maxTeams);
                lastJoinedTeamIndex = teams.size() - 1;
                teamToBench = teams.get(lastJoinedTeamIndex);
                List<Player> players = teamToBench.getTeamMembers();
                benchPlayers.addAll(players);
                log.debug("Benched {}", players);
            }

            log.debug("Removing team {} with ID {}", teamToBench.getTeamName(), teamToBench.getTeamId());
            teams.remove(teamToBench);
        }
    }


    /***
     * Regenerates a new unique room code and sets it as this room's code.
     *
     * @return
     */
    public void regenerateRoomCode() {
        this.roomCode = generateRoomCode();
    }

    //========== private methods ================/

    private void createEmptyTeams(int numOfTeams) {
        for (int i = 1; i <= numOfTeams; i++) {
            Team newTeam = new Team("Empty Slot");
            this.teams.add(newTeam);
        }
    }

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
        log.info("Created room code {}", generatedRoomCode);
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

        log.info("Player {} with ID {} is inside the room: {}", player.getName(), player.getId(), isPlayerInRoom);
        return isPlayerInRoom;
    }

    /***
     *
     * Returns true if the player is inside a team.
     *
     * @param player the player to check all teams with
     * @return a boolean value if the player is in a team or not
     */
    private boolean isPlayerInATeam(Player player) {
        boolean isPlayerInATeam = teams
                .stream()
                .anyMatch(team ->
                        (team.isPlayerInTeam(player)));

        log.info("Player is in a team: {}", isPlayerInATeam);
        return isPlayerInATeam;
    }


}
