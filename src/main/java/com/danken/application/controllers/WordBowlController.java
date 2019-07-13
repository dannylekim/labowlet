package com.danken.application.controllers;

import java.util.List;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.WordBowlInputState;
import com.danken.sessions.GameSession;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class WordBowlController {

    private final GameSession gameSession;

    private final MessageSocketSender sender;


    @Inject
    public WordBowlController(final GameSession userGameSession, final MessageSocketSender sender) {
        this.gameSession = userGameSession;
        this.sender = sender;
    }

    @MessageMapping("/room/{code}/addWords")
    @SendTo("/room/{code}/addWords")
    public WordBowlInputState addWords(@RequestBody List<String> inputWords) {
        var currentRoom = gameSession.getCurrentRoom();
        var game = currentRoom.getGame();

        if (game == null) {
            throw new IllegalStateException("Game hasn't started yet");
        }

        game.addWordBowl(inputWords, gameSession.getPlayer());
        sender.sendStateMessage(currentRoom.getRoomCode(), game.getState());

        return game.getState();
    }


}
