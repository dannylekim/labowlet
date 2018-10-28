package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.RoomSettings;
import com.danken.business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CreateTeamTest {


    RoomSettings roomSettings;
    Player host;
    Room room;



    @BeforeEach
    public void setUp(){
       roomSettings = mock(RoomSettings.class);
       host = mock(Player.class);
       room = spy(new Room(host, roomSettings));

    }

    /***
     * You cannot create more than what the settings has stated
     *
     */
    @Test
    public void maxTeamsTest(){
        doReturn(0).when(roomSettings).getMaxTeams();
        assertThrows(IllegalStateException.class, () -> room.createTeam("test", host));
    }

    /***
     *
     * You can not have the same player in two teams via some creation bug ie if a player is in a team, he will leave
     * that team when he creates a new one
     */
    @Test
    public void noDuplicatePlayerTest(){
        doReturn(5).when(roomSettings).getMaxTeams();
        room.createTeam("One", host);
        room.createTeam("Two", host);

        List<Team> teamsWithMock = room
                .getTeams()
                .stream()
                .filter(team -> team.getTeamMember1() == host || team.getTeamMember2() == host)
                .collect(Collectors.toList());

        assertTrue(teamsWithMock.size() == 1);
    }

    /***
     * The player must leave the bench if he creates a team
     *
     */
    @Test
    public void playerLeavesBenchIfHeCreatesATeam(){
        doReturn(5).when(roomSettings).getMaxTeams();
        room.createTeam("One", host);
        assertTrue(room.getBenchPlayers().size() == 0);
    }

    /***
     * No two teams can have the same team name
     *
     */
    @Test
    public void noSameTeamNameTest(){
        doReturn(2).when(roomSettings).getMaxTeams();
        room.createTeam("One", host);
        assertThrows(IllegalArgumentException.class, () -> room.createTeam("One", host));
    }

    /***
     *  Players must be in the room to even create a room.
     *
     */
    @Test
    public void playerMustBeInRoomTest(){
        doReturn(2).when(roomSettings).getMaxTeams();
        Player player = new Player("test");
        assertThrows(IllegalStateException.class, () -> room.createTeam("One", player));
    }

}
