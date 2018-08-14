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

    @RequestMapping(method = POST, value = "/join")
    public Room joinRoom(@RequestParam String roomCode) {
        Player player = applicationState
                .getGameSession(session)
                .getPlayer();
        Room roomToJoin = applicationState.getRoom(roomCode);
        //fixme log and error handle
        roomToJoin.addPlayer(player);
        return roomToJoin;

    }
}
