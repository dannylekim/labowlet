package com.danken.application.controllers;

import java.util.List;

import com.danken.business.Game;
import com.danken.business.WordBowlInputState;
import com.danken.utility.SocketSessionUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class WordBowlController {

    @MessageMapping("/room/{code}/addWords")
    @SendTo("/client/room/{code}/addWords")
    public WordBowlInputState addWords(@RequestBody List<String> inputWords, SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        var game = currentRoom.getGame();

        if (game == null) {
            throw new IllegalStateException("Game hasn't started yet");
        }

        game.addWordBowl(inputWords, SocketSessionUtils.getSession(accessor).getPlayer());
        return game.getState();
    }

    @MessageMapping("/room/{code}/skipWord")
    @SendTo("/client/room/{code}/game")
    public Game skipWord(SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        if (!currentRoom.getRoomSettings().isAllowSkips()) {
            throw new IllegalStateException("No skipping allowed!");
        }

        final var currentGame = currentRoom.getGame();
        var gameSession = SocketSessionUtils.getSession(accessor);
        if (!gameSession.getPlayer().equals(currentGame.getCurrentActor())) {
            throw new IllegalStateException("Only the actor can skip!");
        }
        final var currentRound = currentGame.getCurrentRound();
        currentRound.getRandomWord();
        return currentGame;

    }

    @MessageMapping("/room/{code}/word")
    @SendTo("/client/room/{code}/game")
    public Game getNewWord(final String word, SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var currentGame = currentRoom.getGame();

        var gameSession = SocketSessionUtils.getSession(accessor);

        if (!gameSession.getPlayer().equals(currentGame.getCurrentActor())) {
            throw new IllegalStateException("Only the actor can create new word!");
        }

        final var currentRound = currentGame.getCurrentRound();
        currentRound.removeWord(word);
        currentRound.getRandomWord();
        return currentGame;
    }


}
