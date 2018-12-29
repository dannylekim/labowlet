package com.danken.application.controllers;

import com.danken.business.OutputMessage;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import com.danken.sessions.GameSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Arrays;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@RequestMapping("/host")
public class HostController {

    private SimpMessagingTemplate template;
    private GameSession userGameSession;

    //Retrieve Application State
    @Inject
    public HostController(SimpMessagingTemplate template, GameSession userGameSession) {
        this.template = template;
        this.userGameSession = userGameSession;
    }

    @RequestMapping(method = PUT, value = "/rooms")
    public Room updateRoom(@RequestBody RoomSettings updatedRoomSettings){

        log.info("Verifying the round types for {}", Arrays.toString(updatedRoomSettings.getRoundTypes().toArray()));
        updatedRoomSettings.verifyRoundTypes();
        Room currentRoom = userGameSession.getCurrentRoom();
        currentRoom.updateRoom(updatedRoomSettings);

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
