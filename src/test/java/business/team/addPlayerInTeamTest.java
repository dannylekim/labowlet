package business.team;

import business.Player;
import business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class addPlayerInTeamTest {


    @Test
    public void setTeamMember1IfNotFull(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        team.setTeamMember1(null);
        assertTrue(team.addPlayerInTeam(secondPlayer));
        assertEquals(secondPlayer, team.getTeamMember1());
    }

    @Test
    public void setTeamMember2IfSetAfterStart(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        assertTrue(team.addPlayerInTeam(secondPlayer));
        assertEquals(secondPlayer, team.getTeamMember2());
    }

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

    @Test
    public void returnFalseIfFull(){
        Team team = new Team("test", mock(Player.class));
        Player secondPlayer = mock(Player.class);
        team.addPlayerInTeam(secondPlayer);

        Player thirdPlayer = mock(Player.class);
        assertFalse(team.addPlayerInTeam(thirdPlayer));
    }
}
