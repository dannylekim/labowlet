package com.danken.application.controllers;

import com.danken.business.FullGameState;
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

        if (gameSession.getPlayer() == null) {
            return null;
        }

        final var fullGameState = new FullGameState();
        fullGameState.setPlayer(gameSession.getPlayer());
        final var currentRoom = gameSession.getCurrentRoom();
        fullGameState.setRoom(currentRoom);
        final var game = currentRoom.getGame();
        if (game != null) {
            fullGameState.setWordState(game.getState());
            fullGameState.setGame(game);
        }

        return fullGameState;
    }

}
