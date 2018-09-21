package application.controllers;

import business.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    HttpSession session;

    //Retrieve Application State
    public PlayerController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method=POST, value="/players")
    public Player createPlayer(@RequestBody Player playerWithJustName){
        //A game session creates a player on instantiation.
        logger.info("Creating userGame session for " + session.getId() + " with the name " + playerWithJustName.getName());
        GameSession userGameSession = new GameSession(playerWithJustName.getName());
        session.setAttribute("gameSession", userGameSession);
        return userGameSession.getPlayer();
    }

}

