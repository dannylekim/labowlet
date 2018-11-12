package com.danken.application.controllers;

import com.danken.application.LabowletState;
import com.danken.business.OutputMessage;
import com.danken.business.room.Room;
import com.danken.business.RoomSettings;
import com.danken.business.room.RoomService;
import com.danken.sessions.GameSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@RequestMapping("/host")
public class HostController {

    private SimpMessagingTemplate template;
    private final LabowletState applicationState = LabowletState.getInstance();
    HttpSession session;
    RoomService roomService;

    //Retrieve Application State
    @Autowired
    public HostController(HttpSession session, RoomService roomService, SimpMessagingTemplate template) {
        this.template = template;
        this.session = session;
        this.roomService = roomService;
    }

    @RequestMapping(method = PUT, value = "/rooms")
    public Room updateRoom(@RequestBody RoomSettings updatedRoomSettings){

        log.info("Verifying the round types for {}", Arrays.toString(updatedRoomSettings.getRoundTypes().toArray()));
        updatedRoomSettings.verifyRoundTypes();
        GameSession userGameSession = applicationState.getGameSession(session);
        Room currentRoom = userGameSession.getCurrentRoom();
        roomService.updateRoom(updatedRoomSettings, currentRoom);

        //Sending the room in a message to allow everyone connected to the socket to be able sync
        log.debug("Sending room to all sockets connecting into /room/{}", currentRoom.getRoomCode());
        template.convertAndSend("/room/" + currentRoom.getRoomCode(), new OutputMessage("ROOM", currentRoom));

        return currentRoom;
    }

    @RequestMapping(method = PUT, value = "/rooms/states")
    public Room updateRoomState(@RequestBody Room roomWithState){
        return null;
    }



}
