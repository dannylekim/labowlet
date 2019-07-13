package com.danken.application.controllers;

import javax.inject.Inject;

import com.danken.application.config.MessageSocketSender;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.Team;
import com.danken.sessions.GameSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

//todo rather than in every method check if the room is null, have it inside a filter that verifies these paths.

/***
 *
 * This controller is responsible for all paths regarding the team.
 *
 */
@RestController
@Slf4j
@RequestMapping("/teams")
public class TeamController {

    private final MessageSocketSender sender;

    private final GameSession userGameSession;

    //Retrieve Application State
    @Inject
    public TeamController(final MessageSocketSender sender, final GameSession userGameSession) {
        this.sender = sender;
        this.userGameSession = userGameSession;
    }

    @PostMapping
    public Room createTeam(@RequestBody Team teamWithOnlyTeamName) {
        final Player player = userGameSession.getPlayer();
        final Room currentRoom = userGameSession.getCurrentRoom();

        log.info("Creating a team with team name {} and the player {}", teamWithOnlyTeamName.getTeamName(), player.getName());
        currentRoom.createTeam(teamWithOnlyTeamName.getTeamName(), player);

        sender.sendRoomMessage(currentRoom);

        return currentRoom;
    }

    @PutMapping("/{teamId}") //add a teamId param
    public Room updateTeam(@RequestBody Team teamWithOnlyTeamName, @PathVariable("teamId") String teamId) throws Exception {

        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();

        if (teamId.equalsIgnoreCase("bench")) {
            boolean isPlayerRemoved = currentRoom.removePlayer(player);
            if (isPlayerRemoved) {
                currentRoom.addPlayerToBench(player);
            }
        } else {
            Team team = currentRoom.getTeam(teamId);
            if (team == null) {
                log.warn("Tried to join a team with team id {} and the current Room Code: {} ", teamId, currentRoom.getRoomCode());
                throw new IllegalArgumentException("There is no team with the specified ID. Please choose a valid team.");
            }
            //if player is inside the team, then the only thing possible to update is the teamName.
            boolean isPlayerInTeam = team.isPlayerInTeam(player);

            log.debug("Is the player " + player.getName() + "inside the team: " + isPlayerInTeam);
            if (isPlayerInTeam && teamWithOnlyTeamName != null) {
                log.info("Setting the team name from {} to {}", team.getTeamName(), teamWithOnlyTeamName.getTeamName());
                team.setTeamName(teamWithOnlyTeamName.getTeamName()); //you are only allowed to update teamName if you are ALREADY inside the team
            } else if (!isPlayerInTeam) {
                log.info("Adding the player {} to the team {}", player.getName(), team.getTeamName());
                currentRoom.addPlayerToTeam(team, player);
            } else {
                //this is an error that should not occur, and if it does then you have to fail gracefully
                log.error("Unknown Error. The team's current players are: {}", team.getTeamMembers());
                throw new IllegalStateException("Unknown Error. This will only occur if for some reason the team name is non-existent and that there are players in the team.");
            }

        }


        sender.sendRoomMessage(currentRoom);
        return currentRoom;
    }

}
