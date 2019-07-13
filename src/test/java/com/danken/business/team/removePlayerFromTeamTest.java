package com.danken.business.team;

import com.danken.business.Player;
import com.danken.business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class removePlayerFromTeamTest {


    /***
     * Test to check that the player is removed from the first slot that he was placed in after instantiation
     *
     */
    @Test
    void removePlayerInTeamMember1() {
        Player player = new Player();

        Team team = new Team("test");
        team.addPlayerInTeam(player);
        assertTrue(team.removePlayerFromTeam(player));
        assertFalse(team.isPlayerInTeam(player));


    }

    /***
     *  Return false because no player was removed from the team
     *
     */
    @Test
    void playerNotInTeam() {
        Player player = new Player();
        Team team = new Team("test");
        team.addPlayerInTeam(player);

        Player playerNotInTeam = new Player();

        assertFalse(team.removePlayerFromTeam(playerNotInTeam));
    }
}
