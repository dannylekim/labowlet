package com.danken.application.controllers;

import java.util.Arrays;

import javax.inject.Inject;

import com.danken.LabowletState;
import com.danken.application.config.MessageSocketSender;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import com.danken.sessions.GameSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***
 *
 * This controller handles all requests that refer to the room and its state.
 *
 */
@RestController
@Slf4j
@RequestMapping("/rooms")
public class RoomController {

    private final MessageSocketSender sender;

    private final LabowletState applicationState = LabowletState.getInstance();

    private final GameSession userGameSession;


    //Retrieve Application State
    @Inject
    public RoomController(final MessageSocketSender sender, final GameSession userGameSession) {
        this.sender = sender;
        this.userGameSession = userGameSession;
    }

    @PostMapping
    public Room createRoom(@RequestBody RoomSettings newRoomSettings) {
        log.info("Verifying the round types for " + Arrays.toString(newRoomSettings.getRoundTypes().toArray()));
        newRoomSettings.verifyRoundTypes();
        Player host = userGameSession.getPlayer();

        log.info("Creating new room for the host {}", host.getName());
        Room newRoom = new Room(host, newRoomSettings);

        log.info("Verifying if the code is unique");
        boolean isRoomCodeUnique = applicationState.isRoomCodeUnique(newRoom.getRoomCode());

        /*if the room code isn't unique, regenerate the room code and check again until there are no rooms with the same
        room code.
        */

        while (!isRoomCodeUnique) {
            newRoom.regenerateRoomCode();
            isRoomCodeUnique = applicationState.isRoomCodeUnique(newRoom.getRoomCode());
        }

        //Sending the room in a message to allow everyone connected to the socket to be able sync
        log.info("Adding newly formed room with room code {} as active for the session", newRoom.getRoomCode());
        applicationState.addActiveRoom(newRoom);
        userGameSession.setCurrentRoom(newRoom);
        sender.sendRoomMessage(newRoom);


        return newRoom;
    }


    @PutMapping
    public Room joinRoom(@RequestBody RoomCode roomCode) {
        Player player = userGameSession.getPlayer();
        Room roomToJoin = applicationState.getRoom(roomCode.getCode());

        if (roomToJoin == null) {
            log.warn("Tried to access the room with room code: {}", roomCode.getCode());
            throw new IllegalArgumentException("There is no room with that room code. Please try again!");

        }

        log.info("Adding player {} to the list of bench players and setting their current room to the session,", player.getName());
        roomToJoin.addPlayerToBench(player);
        userGameSession.setCurrentRoom(roomToJoin);

        //Sending the room in a message to allow everyone connected to the socket to be able sync
        log.debug("Sending room to all sockets connecting into /room/{}", roomToJoin.getRoomCode());
        sender.sendRoomMessage(roomToJoin);


        return roomToJoin;
    }

    static class RoomCode {

        @Getter
        @Setter
        String code;
    }


}


