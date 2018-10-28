package com.danken.application.controllers;

import com.danken.application.LabowletState;
import com.danken.business.OutputMessage;
import com.danken.business.Player;
import com.danken.business.room.Room;
import com.danken.business.Team;
import com.danken.business.room.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
@Slf4j
@RequestMapping("/teams")
public class TeamController {

    private SimpMessagingTemplate template;
    private final LabowletState applicationState = LabowletState.getInstance();
    private HttpSession session;
    private RoomService roomService;


    //Retrieve Application State
    @Autowired
    public TeamController(HttpSession session, SimpMessagingTemplate template, RoomService roomService) {
        this.session = session;
        this.template = template;
        this.roomService = roomService;
    }

    @RequestMapping(method = POST)
    public Room createTeam(@RequestBody Team teamWithOnlyTeamName) {
        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();

        log.info("Creating a team with team name {} and the player {}", teamWithOnlyTeamName.getTeamName(), player.getName());
        roomService.createTeam(teamWithOnlyTeamName.getTeamName(), player, currentRoom);

        //Sending the room in a message to allow everyone connected to the socket to be able sync
        log.debug("Sending room to all sockets connecting into /room/{}" + currentRoom.getRoomCode());
        template.convertAndSend("/room/" + currentRoom.getRoomCode(), new OutputMessage("ROOM", currentRoom));

        return currentRoom;
    }

    @RequestMapping(method = PUT, value = "/{teamId}") //add a teamId param
    public Room updateTeam(@RequestBody Team teamWithOnlyTeamName, @PathVariable("teamId") String teamId) throws Exception {

        GameSession userGameSession = applicationState.getGameSession(session);
        Player player = userGameSession.getPlayer();
        Room currentRoom = userGameSession.getCurrentRoom();

        if(teamId.equalsIgnoreCase("bench")){
            boolean isPlayerRemoved = roomService.removePlayer(player, currentRoom);
            if(isPlayerRemoved){
                roomService.addPlayerToBench(player, currentRoom);
            }
        }
        else {
            Team team = roomService.getTeam(teamId, currentRoom.getTeams());
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
                log.info("Adding the player {} to the team ", player.getName(), team.getTeamName());
                roomService.addPlayerToTeam(currentRoom, team, player);
            } else {
                //this is an error that should not occur, and if it does then you have to fail gracefully
                log.error("Unknown Error. Team name parameter is {}, and the team's current players are {} and {}", teamWithOnlyTeamName.getTeamName(), team.getTeamMember1(), team.getTeamMember2());
                throw new Exception("Unknown Error. This will only occur if for some reason the team name is non-existent and that there are players in the team.");
            }

        }


        //Sending the room in a message to allow everyone connected to the socket to be able sync
        log.debug("Sending room to all sockets connecting into /room/{}" + currentRoom.getRoomCode());
        template.convertAndSend("/room/" + currentRoom.getRoomCode(), new OutputMessage("ROOM", currentRoom));
        return currentRoom;
    }

}
