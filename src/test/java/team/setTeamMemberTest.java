package team;

import business.Player;
import business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class setTeamMemberTest {

    @Test
    public void setSameTeamMemberFrom1In2(){
        Player player = new Player("test");

        Team team = new Team("test", player);
        team.setTeamMember2(player);
        assertNull(team.getTeamMember2());

    }

    @Test
    public void setTeamMember1ToNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        assertNull(team.getTeamMember1());
    }

    @Test
    public void setTeamMember1ToNullWhenAlreadyNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember1(null);
        assertNull(team.getTeamMember1());
    }

    @Test
    public void setTeamMember2ToNullWhenAlreadyNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember2(null);
        assertNull(team.getTeamMember2());
    }

    @Test
    public void setTeamMember2ToNull(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember2(player);
        team.setTeamMember2(null);
        assertNull(team.getTeamMember2());
    }

    @Test
    public void setSameTeamMemberFrom2In1(){
        Player player = new Player("test");
        Team team = new Team("test", player);
        team.setTeamMember1(null);
        team.setTeamMember2(player);
        team.setTeamMember1(player);
        assertNull(team.getTeamMember1());
    }

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
