package com.danken.application.controllers;

import java.util.List;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.Game;
import com.danken.business.Room;
import com.danken.business.WordBowlInputState;
import com.danken.business.WordMessage;
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
        final var player = SocketSessionUtils.getSession(accessor).getPlayer();

        game.addWordBowl(inputWords, player);
        return game.getState();
    }

    @MessageMapping("/room/{code}/game/skipWord")
    @SendTo("/client/room/{code}/game/word")
    public WordMessage skipWord(SimpMessageHeaderAccessor accessor) {
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

        return new WordMessage(currentRound.getRandomWord(), currentRound.getRemainingWords().size());
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
        currentGame.getCurrentTeam().getTeamScore().addPoint(currentRound.getRoundName(), word);

        if (currentRound.getRemainingWords().isEmpty()) {
            handleGameChange(currentRoom, currentGame);
        } else {
            sender.sendWordMessage(currentRoom.getRoomCode(), new WordMessage(currentRound.getRandomWord(), currentRound.getRemainingWords().size()));
        }

    }
    private void handleGameChange(Room currentRoom, Game currentGame) {
        if (currentGame.getCurrentRoundIndex() == currentGame.getRounds().size() - 1) {
            currentGame.setTimeRemaining(-1);
            currentGame.setGameOver(true);
            sender.sendGameOverMessage(currentRoom.getRoomCode(), currentGame.fetchScoreboard());
            sender.sendTimerMessage(currentRoom.getRoomCode(), -1);

        } else {
            sendNewRound(currentGame, currentRoom);
        }
    }
    private void sendNewRound(final Game currentGame, final Room currentRoom) {
        currentGame.setCurrentRoundIndex(currentGame.getCurrentRoundIndex() + 1);
        currentGame.setTimeToCarryOver(currentGame.getTimeRemaining());
        currentGame.setTimeRemaining(-1);
        currentGame.setCurrentRoundActivePlayers();
        sender.sendTimerMessage(currentRoom.getRoomCode(), -1);
        sender.sendGameMessage(currentRoom.getRoomCode(), currentGame);
    }

    @MessageMapping("/room/{code}/game/startStep")
    public void startStep(final SimpMessageHeaderAccessor accessor) throws InterruptedException {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var game = currentRoom.getGame();
        var currentRound = game.getCurrentRound();
        sender.sendWordMessage(currentRoom.getRoomCode(), new WordMessage(currentRound.getRandomWord(), currentRound.getRemainingWords().size()));

        setGameTimeRemaining(currentRoom, game);

        while (game.getTimeRemaining() > 0) {
            Thread.sleep(1000);
            int timeRemaining = game.getTimeRemaining();
            sender.sendTimerMessage(currentRoom.getRoomCode(), --timeRemaining);
            game.setTimeRemaining(timeRemaining);
        }
        if (game.getTimeRemaining() == 0) {
            handleNextTurn(game);
            sender.sendGameMessage(currentRoom.getRoomCode(), game);
        }
    }

    @MessageMapping("/room/{code}/game/endTurn")
    @SendTo("/client/room/{code}/game")
    public Game endTurn(final SimpMessageHeaderAccessor accessor) {
        log.info("Ending turn...");
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var game = currentRoom.getGame();
        handleNextTurn(game);

        return game;

    }

    @MessageMapping("/room/{code}/game/resetGame")
    public void resetGame(final SimpMessageHeaderAccessor accessor) {
        final var currentRoom = SocketSessionUtils.getRoom(accessor);

        if (currentRoom.getGame() != null && currentRoom.getGame().isGameOver()) {
            currentRoom.setGame(null);
            sender.sendResetMessage(currentRoom.getRoomCode());
            sender.sendRoomMessage(currentRoom);
        }


    }

    private void setGameTimeRemaining(Room currentRoom, Game game) {
        if (game.getTimeToCarryOver() > 0) {
            game.setTimeRemaining(game.getTimeToCarryOver());
            game.setTimeToCarryOver(0);
        } else {
            game.setTimeRemaining((int) currentRoom.getRoomSettings().getRoundTimeInSeconds());

        }
    }
    private void handleNextTurn(Game game) {
        game.getCurrentRound().increaseTurnCounter();
        game.setCurrentRoundActivePlayers();
        game.setTimeToCarryOver(0);
    }


}
