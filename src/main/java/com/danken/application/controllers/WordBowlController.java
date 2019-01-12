package com.danken.application.controllers;

import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.WordBowlInputState;
import com.danken.sessions.GameSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

import java.util.List;



@Controller
@Slf4j
public class WordBowlController {

    private GameSession gameSession;
    private SimpMessagingTemplate template;

    @Inject
    public WordBowlController(GameSession userGameSession, SimpMessagingTemplate template) {
        this.gameSession = userGameSession;
        this.template = template;
    }

    @MessageMapping("/room/{roomCode}/addWords")
    @SendTo("/room/{roomCode}/addWords")
    public WordBowlInputState addWords(@RequestBody List<String> inputWords) {
        var currentRoom = gameSession.getCurrentRoom();
        var game = currentRoom.getGame();

        if (game == null) {
            throw new IllegalStateException("Game hasn't started yet");
        }

        game.addWordBowl(inputWords, gameSession.getPlayer());
//        template.convertAndSend("/room/" + currentRoom.getRoomCode() + "/game", game.getState());

        return game.getState();
    }

    @MessageMapping("/host/{roomCode}/start")
    public Player startGame() {
        var player = new Player();
        player.setName("NANI KORE");

        log.info(gameSession.toString());
        template.convertAndSend("/host/" + gameSession.getCurrentRoom().getRoomCode() + "/start", player);

        return player;
    }


}
