package application.controllers;

import application.LabowletState;
import business.Player;
import business.Room;
import business.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sessions.GameSession;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

//todo rather than in every method check if the room is null, have it inside a filter that verifies these paths.

/***
 *
 * This controller is responsible for all paths regarding the team.
 *
 */
@RestController
public class TeamController {

    private LabowletState applicationState;
    //todo logger

    @Autowired
    HttpSession session;

    public TeamController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method = POST, value = "/teams")
    public Room createTeam(@RequestParam String teamName) {
        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();
        currentRoom.createTeam(teamName, player);
        return currentRoom;
    }

    @RequestMapping(method = PUT, value = "/teams/{teamId}") //add a teamId param
    public Team updateTeam(@RequestBody Team teamWithOnlyTeamName, @PathVariable("teamId") String teamId) throws Exception{

        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();

        Team team = currentRoom.getTeam(teamId);
        if(team == null){
            throw new IllegalArgumentException("There is no team with the specified ID. Please choose a valid team.");
        }
        //if player is inside the team, then the only thing possible to update is the teamName.
        boolean isPlayerInTeam = team.isPlayerInTeam(player);

        if(isPlayerInTeam && teamWithOnlyTeamName != null){
            team.setTeamName(teamWithOnlyTeamName.getTeamName()); //you are only allowed to update teamName if you are ALREADY inside the team
        }
        else if (!isPlayerInTeam){
            currentRoom.addPlayerToTeam(team, player);
        }
        else {
            //this is an error that should not occur, and if it does then you have to fail gracefully
            throw new Exception("Unknown Error. This will only occur if for some reason the team name is non-existent and that there are players in the team."); 
        }


        return team;
    }

}
