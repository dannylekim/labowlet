package application.controllers;

import application.LabowletState;
import business.Player;
import business.Room;
import business.RoomSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sessions.GameSession;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/***
 *
 * This controller handles all requests that refer to the room and its state.
 *
 */
@RestController
public class RoomController {

    private LabowletState applicationState;
    //todo logger

    @Autowired
    HttpSession session;

    public RoomController() {
        applicationState = LabowletState.getInstance();
    }



    @RequestMapping(method = POST, value = "/room")
    public Room createRoom(RoomSettings newRoomSettings) {
        GameSession userSession = applicationState.getGameSession(session);
        Player host = userSession.getPlayer();
        Room newRoom = new Room(host, newRoomSettings);
        boolean isRoomCodeUnique = applicationState.isRoomCodeUnique(newRoom.getRoomCode());

        /*if the room code isn't unique, regenerate the room code and check again until there are no rooms with the same
        room code.
        */

        while(!isRoomCodeUnique){
            newRoom.regenerateRoomCode();
            isRoomCodeUnique = applicationState.isRoomCodeUnique(newRoom.getRoomCode());
        }

        applicationState.addActiveRoom(newRoom);
        userSession.setCurrentRoom(newRoom);

        return newRoom;
    }

    @RequestMapping(method = PUT, value = "/room")
    public Room updateRoom(RoomSettings updatedRoomSettings){

        GameSession userGameSession = applicationState.getGameSession(session);
        Room currentRoom = userGameSession.getCurrentRoom();

        //You cannot update a room if it is currently in play or locked
        if(currentRoom.isInPlay() || currentRoom.isLocked()) {
            //todo log and error handle
            throw new IllegalStateException("This room is no longer able to be modified as it has already started.");
        }

        currentRoom.updateRoom(updatedRoomSettings);
        return currentRoom;
    }

    @RequestMapping(method = POST, value = "/join")
    public Room joinRoom(@RequestParam String roomCode) {
        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room roomToJoin = applicationState.getRoom(roomCode);

        if(roomToJoin == null) {
            //todo log and error handle
            throw new IllegalArgumentException("There is no room with that room code. Please try again!");
        }

        roomToJoin.addPlayerToBench(player);
        userGameSession.setCurrentRoom(roomToJoin);
        return roomToJoin;
    }

}
