package com.danken.application.controllers;

import com.danken.business.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danken.sessions.GameSession;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;


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

    private HttpSession session;

    @Inject
    public PlayerController(HttpSession session) {
        this.session = session;
    }

    @RequestMapping(method=POST)
    public Player createPlayer(@RequestBody Player player){
        //A game session creates a player on instantiation.
        log.info("Creating userGame session for {} with the name {} and id {}", session.getId(), player.getName(), player.getId());
        var userGameSession = new GameSession();
        userGameSession.setPlayer(player);
        //todo check if there's already a session, if there is simply change the name so as to not recreate the ID
        session.setAttribute("gameSession", userGameSession);
        return userGameSession.getPlayer();
    }

}

