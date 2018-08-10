package servlet;

import business.Player;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sessions.LabowletSession;
import sessions.LabowletSessionRepository;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class LabowletController {

    @RequestMapping(method=POST, value="/createPlayer")
    public Player createPlayer(@RequestParam String name){

        return new Player(name);
    }

    @RequestMapping("/")
    public Player getSessionId(HttpSession session) {
        return ((LabowletSession) session).getPlayer();
    }

}
