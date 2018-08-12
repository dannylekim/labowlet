package application.controllers;

import business.Player;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import application.LabowletState;
import sessions.PlayerSession;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class PlayerController {

    private LabowletState applicationState;

    //Retrieve Application State
    public PlayerController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method=POST, value="/player")
    public Player createPlayer(@RequestParam String name, HttpSession session){
        PlayerSession userPlayerSession = new PlayerSession(name);
        session.setAttribute("gameSession", userPlayerSession);
        return userPlayerSession.getPlayer();
    }
}
