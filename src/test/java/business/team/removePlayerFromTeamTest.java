package business.team;

import business.Player;
import business.Team;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class removePlayerFromTeamTest {


    @Test
    public void playerInTeamMember1(){
        Player player = new Player("test");

        Team team = new Team("test", player);
        assertTrue(team.removePlayerFromTeam(player));
        assertNull(team.getTeamMember1());

    }

    @Test
    public void playerInTeamMember2(){
        Player player = new Player("test");
        Team team = new Team("test", player);

        team.setTeamMember1(null);
        team.setTeamMember2(player);

        assertTrue(team.removePlayerFromTeam(player));
        assertNull(team.getTeamMember2());
    }

    @Test
    public void playerNotInTeam(){
        Player player = new Player("inTeam");
        Team team = new Team("test", player);

        Player playerNotInTeam = new Player("notInTeam");

        assertFalse(team.removePlayerFromTeam(playerNotInTeam));
    }
}
