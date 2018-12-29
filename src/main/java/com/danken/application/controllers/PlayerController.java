package com.danken.application.controllers;

import com.danken.business.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danken.sessions.GameSession;

import javax.inject.Inject;


import static org.springframework.web.bind.annotation.RequestMethod.*;

/***
 *
 * This controller handles all requests that deal with the player
 *
 */
@RestController
@Slf4j
@RequestMapping("/players")
public class PlayerController {

    private GameSession gameSession;

    @Inject
    public PlayerController(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @RequestMapping(method=POST)
    public Player createPlayer(@RequestBody Player player){
        //A game session creates a player on instantiation.
        gameSession.setPlayer(player);
        return gameSession.getPlayer();
    }

}

