package com.danken.application.controllers;

import com.danken.LabowletState;
import com.danken.business.OutputMessage;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.danken.sessions.GameSession;

import javax.inject.Inject;

import java.util.Arrays;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/***
 *
 * This controller handles all requests that refer to the room and its state.
 *
 */
@RestController
@Slf4j
@RequestMapping("/rooms")
public class RoomController {

    private SimpMessagingTemplate template;
    private final LabowletState applicationState = LabowletState.getInstance();
    private GameSession userGameSession;


    //Retrieve Application State
    @Inject
    public RoomController(SimpMessagingTemplate template, GameSession userGameSession) {
        this.template = template;
        this.userGameSession = userGameSession;
    }

    @RequestMapping(method = POST)
    public Room createRoom(@RequestBody RoomSettings newRoomSettings) {
        log.info("Verifying the round types for " + Arrays.toString(newRoomSettings.getRoundTypes().toArray()));
        newRoomSettings.verifyRoundTypes();
        Player host = userGameSession.getPlayer();

        log.info("Creating new room for the host {}", host.getName());
        Room newRoom = new Room(host, newRoomSettings);

        log.info("Verifying if the roomCode is unique");
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

        log.debug("Sending room to all sockets connecting into /room/{}", newRoom.getRoomCode());
        template.convertAndSend("/room/" + newRoom.getRoomCode(), new OutputMessage(OutputMessage.ROOM_EVENT, newRoom));


        return newRoom;
    }


    @RequestMapping(method = PUT)
    public Room joinRoom(@RequestBody RoomCode roomCode) {
        Player player = userGameSession.getPlayer();
        Room roomToJoin = applicationState.getRoom(roomCode.getRoomCode());

        if (roomToJoin == null) {
            log.warn("Tried to access the room with room code: {}", roomCode.getRoomCode());
            throw new IllegalArgumentException("There is no room with that room code. Please try again!");

        }

        log.info("Adding player {} to the list of bench players and setting their current room to the session,", player.getName());
        roomToJoin.addPlayerToBench(player);
        userGameSession.setCurrentRoom(roomToJoin);

        //Sending the room in a message to allow everyone connected to the socket to be able sync
        log.debug("Sending room to all sockets connecting into /room/{}", roomToJoin.getRoomCode());
        template.convertAndSend("/room/" + roomToJoin.getRoomCode(), new OutputMessage(OutputMessage.ROOM_EVENT, roomToJoin));


        return roomToJoin;
    }


}

class RoomCode {
    @Getter
    @Setter
    String roomCode;

    public RoomCode() {
    }
}

