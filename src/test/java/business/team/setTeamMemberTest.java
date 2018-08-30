package business.team;

import business.Player;
import business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class setTeamMemberTest {

    /***
     *
     * Test that the second slot is null if you try adding the same player from first slot
     *
     */
    @Test
    public void setSameTeamMemberFrom1In2(){
        Player player = new Player("test");

        Team team = new Team("test", player);
        team.setTeamMember2(player);
        assertNull(team.getTeamMember2());

    }

    /***
     *  Assert that you are able to null the first slot
     */
    @Test
    public void setTeamMember1ToNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        assertNull(team.getTeamMember1());
    }

    /***
     * Even if the first slot is null, the slot should be null after inserting a null
     *
     */
    @Test
    public void setTeamMember1ToNullWhenAlreadyNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember1(null);
        assertNull(team.getTeamMember1());
    }

    /***
     * If the second slot is null, then setting it to null will still return null
     *
     */
    @Test
    public void setTeamMember2ToNullWhenAlreadyNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember2(null);
        assertNull(team.getTeamMember2());
    }

    /***
     * Setting the second slot to null even if there was a player in there
     *
     */
    @Test
    public void setTeamMember2ToNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember2(player);
        team.setTeamMember2(null);
        assertNull(team.getTeamMember2());
    }

    /***
     *  Return null for trying to set the first slot the same player as the second slot
     *
     */
    @Test
    public void setSameTeamMemberFrom2In1(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember2(player);
        team.setTeamMember1(player);
        assertNull(team.getTeamMember1());
    }

    /***
     * Set any kind of player if both slots are empty
     *
     */
    @Test
    public void setPlayerInAnyWhenBothNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember1(player);
        assertEquals(player, team.getTeamMember1());

        team = new Team("first", player);
        team.setTeamMember1(null);
        team.setTeamMember2(player);
        assertEquals(player, team.getTeamMember2());
    }

}
