package com.danken.application.controllers;

import java.util.Optional;

import com.danken.business.FullGameState;
import com.danken.business.Room;
import com.danken.business.Team;
import com.danken.sessions.GameSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SessionController {

    private final GameSession gameSession;

    public SessionController(final GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @GetMapping("/game/session")
    public FullGameState getFullGame() {

        log.info("Reconnecting...");
        final var player = gameSession.getPlayer();
        if (player == null) {
            return null;
        }

        final var fullGameState = new FullGameState();
        fullGameState.setPlayer(player);
        final Optional<Room> currentRoom = Optional.ofNullable(gameSession.getCurrentRoom());
        fullGameState.setRoom(currentRoom.orElse(null));
        fullGameState.setCurrentlyIn("LOBBY");


        if (currentRoom.isPresent()) {
            final Team team = currentRoom.get().getTeams().stream().filter(t -> t.isPlayerInTeam(player)).findFirst().orElse(null);
            fullGameState.setTeam(team);
        }

        currentRoom.map(Room::getGame).ifPresent(game -> {
            fullGameState.setWordState(game.getState());
            fullGameState.setGame(game);
            if (game.isStarted() && game.getCurrentScores() != null) {
                fullGameState.setCurrentlyIn("SUMMARY");
            } else if (game.isStarted()) {
                fullGameState.setCurrentlyIn("GAME");
                fullGameState.setCurrentTime(game.getTimeRemaining());
            } else {
                fullGameState.setCurrentlyIn("BOWL");
            }
        });

        log.info("Reconnected with: " + player + " and in: " + fullGameState.getCurrentlyIn());

        return fullGameState;
    }

}
