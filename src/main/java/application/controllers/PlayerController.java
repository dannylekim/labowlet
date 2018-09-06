package application.controllers;

import business.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import application.LabowletState;
import sessions.GameSession;

import javax.servlet.http.HttpSession;


import static org.springframework.web.bind.annotation.RequestMethod.*;

/***
 *
 * This controller handles all requests that deal with the player
 *
 */
@RestController
public class PlayerController {

    private LabowletState applicationState;
    //todo logger

    @Autowired
    HttpSession session;

    //Retrieve Application State
    public PlayerController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method=POST, value="/players")
    public Player createPlayer(@RequestParam String name){
        //A game session creates a player on instantiation.
        GameSession userGameSession = new GameSession(name);
        session.setAttribute("gameSession", userGameSession);
        return userGameSession.getPlayer();

    }

}

