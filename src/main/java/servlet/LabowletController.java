package servlet;

import business.Player;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sessions.GameSession;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class LabowletController {

    private LabowletState applicationState;

    public LabowletController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method=POST, value="/createPlayer")
    public Player createPlayer(@RequestParam String name, HttpSession session){
        //todo create a filter that verifies the header if there is a session or not, if not then create, else do not create
        GameSession userGameSession = new GameSession(name);
        session.setAttribute("gameSession", userGameSession);
        return userGameSession.getPlayer();
    }
}
