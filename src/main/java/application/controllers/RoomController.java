package application.controllers;

import application.LabowletState;
import business.Player;
import business.Room;
import business.RoomSettings;

import business.Team;
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
        applicationState.addActiveRoom(newRoom);
        return newRoom;
    }

    @RequestMapping(method = PUT, value = "/room")
    public RoomSettings updateRoom(RoomSettings updatedRoomSettings){
        return null; //todo
    }

    @RequestMapping(method = POST, value = "/join")
    public Room joinRoom(@RequestParam String roomCode) {
        PlayerSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room roomToJoin = applicationState.getRoom(roomCode);
        //fixme log and error handle
        roomToJoin.addPlayerToBench(player);
        userGameSession.setCurrentRoom(roomToJoin);
        return roomToJoin;
    }

    @RequestMapping(method = POST, value = "/joinTeam")
    public Room joinTeam(@RequestParam String teamId){

        PlayerSession userGameSession = applicationState.getGameSession(session);
        Room room = userGameSession.getCurrentRoom();

        if(room != null){
            Team teamToJoin = room.getTeam(teamId);
            room.addPlayerToTeam(teamToJoin, userGameSession.getPlayer());
        }

        return room;
    }

    @RequestMapping(method = POST, value = "/team")
    public Room createTeam(@RequestParam String teamName) {
        PlayerSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();
        if (currentRoom == null) {
            //todo errorhandle and return a message properly
            return null;
        }
        boolean hasJoined = currentRoom.createTeam(teamName, player);
        if (hasJoined) {
            return currentRoom;
        } else {
            //todo error handle
            return null;
        }
    }

}
