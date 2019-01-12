package com.danken.application.controllers;

import com.danken.business.WordBowlInputState;
import com.danken.sessions.GameSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@Slf4j
@RequestMapping("/wordbowl")
public class WordBowlController {

    private GameSession gameSession;
    private SimpMessagingTemplate template;


    @Inject
    public WordBowlController(SimpMessagingTemplate template, GameSession userGameSession) {
        this.template = template;
        this.gameSession = userGameSession;
    }

    @RequestMapping(method = POST)
    public WordBowlInputState addWords(@RequestBody List<String> inputWords ) {
        var currentRoom = gameSession.getCurrentRoom();
        var game = currentRoom.getGame();

        if(game == null){
            throw new IllegalStateException("Game hasn't started yet");
        }

        game.addWordBowl(inputWords, gameSession.getPlayer());
        template.convertAndSend("/room/" + currentRoom.getRoomCode() + "/game", game.getState());

        return game.getState();
    }





}
