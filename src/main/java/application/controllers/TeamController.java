package application.controllers;

import application.LabowletState;
import business.Player;
import business.Room;
import business.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sessions.PlayerSession;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class TeamController {

    private LabowletState applicationState;

    @Autowired
    HttpSession session;

    public TeamController(){
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method = PUT, value = "/joinTeam/{teamId}")
    public Room joinTeam(@PathVariable("teamId") String teamId){

        PlayerSession userGameSession = applicationState.getGameSession(session);
        Room room = userGameSession.getCurrentRoom();

        if(room != null){
            Team teamToJoin = room.getTeam(teamId);
            if(teamToJoin != null) {
                room.addPlayerToTeam(teamToJoin, userGameSession.getPlayer());
            }
            else {
                //todo error
            }
        }
        else {
            //todo error
        }

        return room;
    }

    @RequestMapping(method = POST, value = "/team")
    public Room createTeam(@RequestParam String teamName) {
        PlayerSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();
        if (currentRoom == null) {
            //todo errorhandle and return a message properly
            return null;
        }
        boolean hasJoined = currentRoom.createTeam(teamName, player);
        if (hasJoined) {
            return currentRoom;
        } else {
            //todo error handle
            return null;
        }
    }

    @RequestMapping(method = PUT, value = "/team/{teamId}") //add a teamId param
    public Team updateTeam(@RequestParam(required = false) String teamName, @PathVariable("teamId") String teamId){

        PlayerSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();
        if(currentRoom == null) {
            //todo error handle
        }

        Team team = currentRoom.getTeam(teamId);
        //if player is inside the team, then the only thing possible to update is the teamName.
        boolean isPlayerInTeam = team.isPlayerInTeam(player);

        if(isPlayerInTeam && teamName != null){
            team.setTeamName(teamName); //you are only allowed to update teamName if you are ALREADY inside the team
        }
        else if (!isPlayerInTeam){
            boolean playerHasJoined = currentRoom.addPlayerToTeam(team, player);
            if(!playerHasJoined) {
                //todo error handle
            }
        }
        else {
            //todo return either an error or nothing
        }


        return team;
    }

}
