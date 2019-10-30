package com.danken.application.controllers;

import java.util.List;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.Game;
import com.danken.business.Player;
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
        log.info("Attempting to skip word...");
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
        log.info("Get new word...");
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var currentGame = currentRoom.getGame();

        var gameSession = SocketSessionUtils.getSession(accessor);

        if (!gameSession.getPlayer().equals(currentGame.getCurrentActor())) {
            throw new IllegalStateException("Only the actor can create new word!");
        }

        final var currentRound = currentGame.getCurrentRound();
        if (currentRound.removeWord(word)) {
            currentGame.getCurrentTeam().getTeamScore().addPoint(currentRound.getRoundName(), word);

            if (currentRound.getRemainingWords().isEmpty()) {
                handleGameChange(currentRoom, currentGame);
            } else {
                sender.sendWordMessage(currentRoom.getRoomCode(), new WordMessage(currentRound.getRandomWord(), currentRound.getRemainingWords().size()));
            }
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
        currentGame.setCurrentScores(currentGame.fetchScoreboard());
        currentGame.setTurnStarted(false);
        sender.sendTimerMessage(currentRoom.getRoomCode(), -1);
        sender.sendGameMessage(currentRoom.getRoomCode(), currentGame);
    }

    @MessageMapping("/room/{code}/game/startStep")
    public void startStep(final SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var game = currentRoom.getGame();
        var currentRound = game.getCurrentRound();

        log.info("Started new step with gameTurnStarted: " + game.isTurnStarted() + " on round: " + currentRound.getRoundName());

        if (game.isTurnStarted()) {
            return;
        }

        sender.sendWordMessage(currentRoom.getRoomCode(), new WordMessage(currentRound.getRandomWord(), currentRound.getRemainingWords().size()));

        setGameTimeRemaining(currentRoom, game);

        handleTimingEvents(currentRoom, game);

        game.setTurnStarted(true);


    }

    private void handleTimingEvents(Room currentRoom, Game game) {
        log.debug("Starting new timing events");
        final Runnable r = () -> {
            while (game.getTimeRemaining() > 0) {
                try {
                    Thread.sleep(1000);
                    int timeRemaining = game.getTimeRemaining();
                    sender.sendTimerMessage(currentRoom.getRoomCode(), --timeRemaining);
                    game.setTimeRemaining(timeRemaining);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }

            }
            if (game.getTimeRemaining() == 0) {
                log.debug("Time set to 0, now resetting to 0, carry to 0 and going to next turn.");
                handleNextTurn(game);
                sender.sendTimerMessage(currentRoom.getRoomCode(), (int) currentRoom.getRoomSettings().getRoundTimeInSeconds());
                sender.sendGameMessage(currentRoom.getRoomCode(), game);
            }
        };

        new Thread(r).start();
    }

    @MessageMapping("/room/{code}/game/endTurn")
    @SendTo("/client/room/{code}/game")
    public Game endTurn(final SimpMessageHeaderAccessor accessor) {
        log.info("Ending the turn...");
        var currentRoom = SocketSessionUtils.getRoom(accessor);
        final var game = currentRoom.getGame();
        handleNextTurn(game);
        sender.sendTimerMessage(currentRoom.getRoomCode(), (int) currentRoom.getRoomSettings().getRoundTimeInSeconds());

        return game;

    }

    @MessageMapping("/room/{code}/game/nextRound")
    @SendTo("/client/room/{code}/game")
    public Game giveGameObjectWithNullCurrentScores(final SimpMessageHeaderAccessor accessor) {
        var currentRoom = SocketSessionUtils.getRoom(accessor);

        log.info("Getting null score game object");

        if (!SocketSessionUtils.getSession(accessor).getPlayer().equals(currentRoom.getHost())) {
            throw new IllegalStateException("Only the host can call this endpoint!");
        }

        final Game game = currentRoom.getGame();
        if (game.getCurrentRound().getRemainingWords().isEmpty()) {
            throw new IllegalStateException("Cannot retrieve object until round is over");
        }

        game.setCurrentScores(null);

        if (game.getTimeToCarryOver() > 0) {
            log.debug("Carrying over the time remaining: " + game.getTimeToCarryOver());
            game.setTimeRemaining(game.getTimeToCarryOver());
        }

        sender.sendTimerMessage(currentRoom.getRoomCode(), game.getTimeRemaining());

        return game;

    }

    @MessageMapping("/room/{code}/game/resetGame")
    public void resetGame(final SimpMessageHeaderAccessor accessor) {
        log.info("Resetting game");
        final var currentRoom = SocketSessionUtils.getRoom(accessor);
        final Player player = SocketSessionUtils.getSession(accessor).getPlayer();

        if (currentRoom.getGame() != null && currentRoom.getGame().isGameOver() && player.equals(currentRoom.getHost())) {
            currentRoom.setGame(null);
            sender.sendResetMessage(currentRoom.getRoomCode());
            sender.sendRoomMessage(currentRoom);
        }


    }

    private void setGameTimeRemaining(Room currentRoom, Game game) {
        if (game.getTimeToCarryOver() > 0) {
            game.setTimeRemaining(game.getTimeToCarryOver());
            game.setTimeToCarryOver(0);
            log.info("Round time remaining: " + game.getTimeRemaining());
        } else {
            log.info("Round time remaining: " + game.getTimeRemaining());
            game.setTimeRemaining((int) currentRoom.getRoomSettings().getRoundTimeInSeconds());

        }
    }
    private void handleNextTurn(Game game) {
        log.info("Handling next turn operations");
        game.getCurrentRound().increaseTurnCounter();
        game.setCurrentRoundActivePlayers();
        game.setTimeToCarryOver(0);
        game.setTimeRemaining(0);
        game.setTurnStarted(false);

    }


}
