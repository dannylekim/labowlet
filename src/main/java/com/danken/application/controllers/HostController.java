package com.danken.application.controllers;

import java.util.Arrays;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import com.danken.business.WordBowlInputState;
import com.danken.sessions.GameSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/host")
public class HostController {

    private final MessageSocketSender sender;

    private final GameSession userGameSession;

    //Retrieve Application State
    @Inject
    public HostController(final MessageSocketSender sender, final GameSession userGameSession) {
        this.sender = sender;
        this.userGameSession = userGameSession;
    }

    @PutMapping("/rooms")
    public Room updateRoom(@RequestBody RoomSettings updatedRoomSettings) {

        log.info("Verifying the round types for {}", Arrays.toString(updatedRoomSettings.getRoundTypes().toArray()));
        updatedRoomSettings.verifyRoundTypes();
        Room currentRoom = userGameSession.getCurrentRoom();
        currentRoom.updateRoom(updatedRoomSettings);

        sender.sendRoomMessage(currentRoom);
        return currentRoom;
    }

    @PostMapping("/setupGame")
    public WordBowlInputState startWordInputState() {
        Room currentRoom = userGameSession.getCurrentRoom();
        var game = currentRoom.createGame();

        sender.sendWordStateMessage(currentRoom.getRoomCode(), game.getState());
        return game.getState();

    }

    @PostMapping("/startGame")
    public boolean gameStart() {
        var currentRoom = userGameSession.getCurrentRoom();
        final var game = currentRoom.getGame();
        final var didGameStart = game.startGame();
        sender.sendGameMessage(currentRoom.getRoomCode(), game);
        return didGameStart;
    }


}
