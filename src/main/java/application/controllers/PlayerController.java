package application.controllers;

import business.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import application.LabowletState;
import sessions.PlayerSession;

import javax.servlet.http.HttpSession;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class PlayerController {

    private LabowletState applicationState;

    @Autowired
    HttpSession session;

    //Retrieve Application State
    public PlayerController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method=POST, value="/player")
    public Player createPlayer(@RequestParam String name){
        PlayerSession userPlayerSession = new PlayerSession(name);
        session.setAttribute("gameSession", userPlayerSession);
        return userPlayerSession.getPlayer();
    }

    @RequestMapping(method=POST, value="/word")
    public Player createWordBowl(@RequestParam List<String> words){
        Player player = applicationState
                .getGameSession(session)
                .getPlayer();

        player.createWordBowl(words);
        return player;
    }

}

