package com.danken.application.controllers;

import java.util.List;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.Game;
import com.danken.business.Room;
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

        if (currentRound.getRemainingWords().isEmpty()) {

            handleGameChange(currentRoom, currentGame);
        } else {
            currentGame.getCurrentTeam().getTeamScore().addPoint(currentRound.getRoundName(), word);
            sender.sendWordMessage(currentRoom.getRoomCode(), currentRound.getRandomWord());
        }

    }
    private void handleGameChange(Room currentRoom, Game currentGame) {
        if (currentGame.getCurrentRoundIndex() == currentGame.getRounds().size() - 1) {
            sender.sendGameOverMessage(currentRoom.getRoomCode(), currentGame.fetchScoreboard());
        } else {
            sendNewRound(currentGame, currentRoom);
        }
    }
    private void sendNewRound(final Game currentGame, final Room currentRoom) {
        currentGame.setCurrentRoundIndex(currentGame.getCurrentRoundIndex() + 1);
        currentGame.setTimeRemaining(-1);
        currentGame.setCurrentRoundActivePlayers();
        sender.sendGameMessage(currentRoom.getRoomCode(), currentGame);
    }

    @MessageMapping("/room/{code}/game/startStep")
    public void startStep(final SimpMessageHeaderAccessor accessor) throws InterruptedException {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var game = currentRoom.getGame();
        sender.sendWordMessage(currentRoom.getRoomCode(), game.getCurrentRound().getRandomWord());

        game.setTimeRemaining((int) currentRoom.getRoomSettings().getRoundTimeInSeconds());

        while (game.getTimeRemaining() > 0) {
            Thread.sleep(1000);
            int timeRemaining = game.getTimeRemaining();
            sender.sendTimerMessage(currentRoom.getRoomCode(), --timeRemaining);
            game.setTimeRemaining(timeRemaining);
        }
        if (game.getTimeRemaining() == 0) {
            handleNextTurn(currentRoom, game);
        }


    }
    private void handleNextTurn(Room currentRoom, Game game) {
        game.setCurrentRoundIndex(game.getCurrentRoundIndex() + 1);
        game.setCurrentRoundActivePlayers();
        game.getCurrentRound().increaseTurnCounter();
        sender.sendGameMessage(currentRoom.getRoomCode(), game);
    }


}
