package application.controllers;

import application.LabowletState;
import business.Player;
import business.Room;
import business.RoomSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sessions.PlayerSession;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class RoomController {

    private LabowletState applicationState;

    @Autowired
    HttpSession session;

    public RoomController() {
        applicationState = LabowletState.getInstance();
    }



    @RequestMapping(method = POST, value = "/room")
    public Room createRoom(RoomSettings newRoomSettings) {
        Player host = applicationState
                .getGameSession(session)
                .getPlayer();
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
        return newRoom;
    }

    @RequestMapping(method = PUT, value = "/room")
    public Room updateRoom(RoomSettings updatedRoomSettings){

        PlayerSession userGameSession = applicationState.getGameSession(session);
        Room currentRoom = userGameSession.getCurrentRoom();

        if(currentRoom.isInPlay()) {
            //todo log and error handle
        }

        currentRoom.updateRoom(updatedRoomSettings);
        return currentRoom;
    }

    @RequestMapping(method = POST, value = "/join")
    public Room joinRoom(@RequestParam String roomCode) {
        PlayerSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room roomToJoin = applicationState.getRoom(roomCode);

        if(roomToJoin == null) {
            //todo log and error handle
        }

        roomToJoin.addPlayerToBench(player);
        userGameSession.setCurrentRoom(roomToJoin);
        return roomToJoin;
    }

}
