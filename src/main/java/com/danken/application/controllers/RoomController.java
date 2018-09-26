package com.danken.application.controllers;

import com.danken.application.LabowletState;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.danken.sessions.GameSession;

import javax.servlet.http.HttpSession;

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
public class RoomController {

    private LabowletState applicationState;

    @Autowired
    HttpSession session;


    public RoomController() {
        applicationState = LabowletState.getInstance();
    }



    @RequestMapping(method = POST, value = "/rooms")
    public Room createRoom(@RequestBody RoomSettings newRoomSettings) {
        log.info("Verifying the round types for " + Arrays.toString(newRoomSettings.getRoundTypes().toArray()));
        newRoomSettings.verifyRoundTypes();
        GameSession userSession = applicationState.getGameSession(session);
        Player host = userSession.getPlayer();

        log.info("Creating new room for the host {}", host.getName());
        Room newRoom = new Room(host, newRoomSettings);

        log.info("Verifying if the roomCode is unique");
        boolean isRoomCodeUnique = applicationState.isRoomCodeUnique(newRoom.getRoomCode());

        /*if the room code isn't unique, regenerate the room code and check again until there are no rooms with the same
        room code.
        */

        while(!isRoomCodeUnique){
            newRoom.regenerateRoomCode();
            isRoomCodeUnique = applicationState.isRoomCodeUnique(newRoom.getRoomCode());
        }

        log.info("Adding newly formed room with room code {} as active for the session", newRoom.getRoomCode());
        applicationState.addActiveRoom(newRoom);
        userSession.setCurrentRoom(newRoom);
        return newRoom;
    }

    @RequestMapping(method = PUT, value = "/host/rooms")
    public Room updateRoom(@RequestBody RoomSettings updatedRoomSettings){

        log.info("Verifying the round types for {}", Arrays.toString(updatedRoomSettings.getRoundTypes().toArray()));
        updatedRoomSettings.verifyRoundTypes();
        GameSession userGameSession = applicationState.getGameSession(session);
        Room currentRoom = userGameSession.getCurrentRoom();
        currentRoom.updateRoom(updatedRoomSettings);
        return currentRoom;
    }

    @RequestMapping(method = PUT, value = "/rooms")
    @ResponseBody
    public Room joinRoom(@RequestBody Room roomWithOnlyRoomCode) {
        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room roomToJoin = applicationState.getRoom(roomWithOnlyRoomCode.getRoomCode());

        if(roomToJoin == null) {
            log.warn("Tried to access the room with room code: {}", roomWithOnlyRoomCode.getRoomCode());
            throw new IllegalArgumentException("There is no room with that room code. Please try again!");

        }

        log.info("Adding player {} to the list of bench players and setting their current room to the session,", player.getName());
        roomToJoin.addPlayerToBench(player);
        userGameSession.setCurrentRoom(roomToJoin);
        return roomToJoin;
    }

}
