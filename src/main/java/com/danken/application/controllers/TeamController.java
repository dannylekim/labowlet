package com.danken.application.controllers;

import com.danken.application.LabowletState;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.danken.sessions.GameSession;

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
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    HttpSession session;

    public TeamController() {
        applicationState = LabowletState.getInstance();
    }

    @RequestMapping(method = POST, value = "/teams")
    public Room createTeam(@RequestBody Team teamWithOnlyTeamName) {
        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();

        logger.info("Creating a team with team name {} and the player {}", teamWithOnlyTeamName.getTeamName(), player.getName());
        currentRoom.createTeam(teamWithOnlyTeamName.getTeamName(), player);
        return currentRoom;
    }

    @RequestMapping(method = PUT, value = "/teams/{teamId}") //add a teamId param
    public Team updateTeam(@RequestBody Team teamWithOnlyTeamName, @PathVariable("teamId") String teamId) throws Exception {

        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();

        Team team = currentRoom.getTeam(teamId);
        if (team == null) {
            logger.warn("Tried to join a team with team id {} and the current Room Code: {} ", teamId, currentRoom.getRoomCode());
            throw new IllegalArgumentException("There is no team with the specified ID. Please choose a valid team.");
        }
        //if player is inside the team, then the only thing possible to update is the teamName.
        boolean isPlayerInTeam = team.isPlayerInTeam(player);

        logger.debug("Is the player " + player.getName() + "inside the team: " + isPlayerInTeam);
        if (isPlayerInTeam && teamWithOnlyTeamName != null) {
            logger.info("Setting the team name from {} to {}", team.getTeamName(), teamWithOnlyTeamName.getTeamName());
            team.setTeamName(teamWithOnlyTeamName.getTeamName()); //you are only allowed to update teamName if you are ALREADY inside the team
        } else if (!isPlayerInTeam) {
            logger.info("Adding the player {} to the team ", player.getName(), team.getTeamName());
            currentRoom.addPlayerToTeam(team, player);
        } else {
            //this is an error that should not occur, and if it does then you have to fail gracefully
            logger.error("Unknown Error. Team name parameter is {}, and the team's current players are {} and {}", teamWithOnlyTeamName.getTeamName(), team.getTeamMember1(), team.getTeamMember2());
            throw new Exception("Unknown Error. This will only occur if for some reason the team name is non-existent and that there are players in the team.");
        }


        return team;
    }

}
