package business.team;

import business.Player;
import business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class removePlayerFromTeamTest {


    /***
     * Test to check that the player is removed from the first slot that he was placed in after instantiation
     *
     */
    @Test
    public void removePlayerInTeamMember1(){
        Player player = new Player("test");

        Team team = new Team("test", player);
        assertAll(() -> {
            assertTrue(team.removePlayerFromTeam(player));
            assertNull(team.getTeamMember1());
        });
       

    }

    /***
     * Test to check that the player is removed from the second slot after being placed into it
     *
     */
    @Test
    public void playerInTeamMember2(){
        Player player = new Player("test");
        Team team = new Team("test", player);

        team.setTeamMember1(null);
        team.setTeamMember2(player);

        assertAll(() -> {
            assertTrue(team.removePlayerFromTeam(player));
            assertNull(team.getTeamMember2());
        });
       
    }

    /***
     *  Return false because no player was removed from the team
     *
     */
    @Test
    public void playerNotInTeam(){
        Player player = new Player("inTeam");
        Team team = new Team("test", player);

        Player playerNotInTeam = new Player("notInTeam");

        assertFalse(team.removePlayerFromTeam(playerNotInTeam));
    }
}
