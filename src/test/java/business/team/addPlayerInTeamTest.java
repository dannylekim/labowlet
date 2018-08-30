package business.team;

import business.Player;
import business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class addPlayerInTeamTest {


    /***
     *  Player should be added to the first slot if not full
     *
     */
    @Test
    public void setTeamMember1IfNotFull(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        team.setTeamMember1(null);
        assertTrue(team.addPlayerInTeam(secondPlayer));
        assertEquals(secondPlayer, team.getTeamMember1());
    }

    /***
     * Player should be added to the second slot if you add a player immediately after instantiation
     *
     */
    @Test
    public void setTeamMember2IfSetAfterStart(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        assertTrue(team.addPlayerInTeam(secondPlayer));
        assertEquals(secondPlayer, team.getTeamMember2());
    }

    /***
     *
     * Player should be added to second slot if not null
     */
    @Test
    public void setTeamMember2IfNotNull(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        Player thirdPlayer = mock(Player.class);
        team.addPlayerInTeam(secondPlayer);
        team.setTeamMember2(null);
        team.addPlayerInTeam(thirdPlayer);
        assertEquals(thirdPlayer, team.getTeamMember2());
    }


    /***
     * return false when the team is full
     *
     */
    @Test
    public void returnFalseIfFull(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        team.addPlayerInTeam(secondPlayer);

        Player thirdPlayer = mock(Player.class);
        assertFalse(team.addPlayerInTeam(thirdPlayer));
    }
}
