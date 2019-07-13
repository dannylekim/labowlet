package com.danken.application.controllers;

import java.util.List;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
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

    private final MessageSocketSender sender;

    @Inject
    public WordBowlController(final MessageSocketSender sender) {
        this.sender = sender;
    }

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

    @MessageMapping("/room/{code}/game/skipWord")
    @SendTo("/client/room/{code}/game/word")
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

        if (currentRound.getRemainingWords().size() <= 1) {
            throw new IllegalStateException("Can not skip the last word!");
        }

        currentRound.getRandomWord();
        return currentGame;
    }

    @MessageMapping("/room/{code}/game/newWord")
    public void getNewWord(final String word, final SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var currentGame = currentRoom.getGame();

        var gameSession = SocketSessionUtils.getSession(accessor);

        if (!gameSession.getPlayer().equals(currentGame.getCurrentActor())) {
            throw new IllegalStateException("Only the actor can create new word!");
        }

        final var currentRound = currentGame.getCurrentRound();
        currentRound.removeWord(word);

        if (currentRound.getRemainingWords().size() == 0) {

            if(currentGame.getCurrentRoundIndex() == currentGame.getRounds().size() - 1){
                //TODO GAME OVER
            }
            else {
                currentGame.setCurrentRoundIndex(currentGame.getCurrentRoundIndex() + 1);
                //TODO stop timer
            }
        } else {
            currentGame.getCurrentTeam().getTeamScore().addPoint(currentRound.getRoundName(), word);
            sender.sendWordMessage(currentRoom.getRoomCode(), currentRound.getRandomWord());
        }

    }

    @MessageMapping("/room/{code}/game/startStep")
    public void startStep(final SimpMessageHeaderAccessor accessor) throws InterruptedException {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        sender.sendWordMessage(currentRoom.getRoomCode(), currentRoom.getGame().getCurrentRound().getRandomWord());

        int seconds = (int) currentRoom.getRoomSettings().getRoundTimeInSeconds();

        //TODO figure this out later situation
        while(seconds != 0){
            Thread.sleep(1000);
            sender.sendTimerMessage(currentRoom.getRoomCode(), --seconds);
        }

    }


}
