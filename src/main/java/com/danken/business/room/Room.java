package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.RoomSettings;
import com.danken.business.Round;
import com.danken.business.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Getter
@NoArgsConstructor
@Slf4j
public class Room {

    // -------- DATA CONSTANTS ------------
    @Setter private List<Team> teams;
    private List<Player> benchPlayers;
    private Player host;
    @Setter private String roomCode; //given a setter to use for @RequestBody
    @Setter private RoomSettings roomSettings;
    private List<String> wordBowl;
    private Map<Player, List<String>> wordsMadePerPlayer;
    private List<Round> rounds;

    //Game state -> fixme should this be refactored into its own object
    @Setter
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
        this.host = host;
        this.roomCode = generateRoomCode();
        this.roomSettings = roomSettings;

        log.info("Created a new room with {} and {}", host.getName(), host.getId());

        //set State// //fixme move this into GameState
        isInPlay = false;
        canStart = false; //todo set

        isLocked = false;
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
     * Regenerates a new unique room code and sets it as this room's code.
     *
     * @return
     */
    public String regenerateRoomCode() {
        this.roomCode = generateRoomCode();
        return roomCode;
    }

    /***
     * Returns true if the player is inside the room (either in the bench of the teams)
     *
     * @param player the player to verify if they are in the room
     * @return a boolean value if player is in the room or not
     */
    public boolean isPlayerInRoom(Player player) {
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
    public boolean isPlayerInATeam(Player player) {
        boolean isPlayerInATeam = teams
                .stream()
                .anyMatch(team ->
                        (team.isPlayerInTeam(player)));

        log.info("Player is in a team: {}", isPlayerInATeam);
        return isPlayerInATeam;
    }


}
