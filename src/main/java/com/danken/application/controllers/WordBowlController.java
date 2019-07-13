package com.danken.application.controllers;

import java.util.List;

import javax.inject.Inject;

import com.danken.business.WordBowlInputState;
import com.danken.sessions.GameSession;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class WordBowlController {

    private final GameSession gameSession;

    private final SimpMessagingTemplate template;


    @Inject
    public WordBowlController(GameSession userGameSession, final SimpMessagingTemplate simpMessagingTemplate) {
        this.gameSession = userGameSession;
        this.template = simpMessagingTemplate;
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
        template.convertAndSend("/room/" + currentRoom.getRoomCode() + "/game", game.getState());

        return game.getState();
    }


}
