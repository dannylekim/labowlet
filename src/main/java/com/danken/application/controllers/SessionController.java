package com.danken.application.controllers;

import java.util.Optional;

import com.danken.business.FullGameState;
import com.danken.business.Room;
import com.danken.business.Team;
import com.danken.sessions.GameSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    private final GameSession gameSession;

    public SessionController(final GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @GetMapping("/game/session")
    public FullGameState getFullGame() {

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
            if (game.isStarted()) {
                fullGameState.setCurrentlyIn("GAME");
            } else {
                fullGameState.setCurrentlyIn("BOWL");
            }
        });

        return fullGameState;
    }

}
