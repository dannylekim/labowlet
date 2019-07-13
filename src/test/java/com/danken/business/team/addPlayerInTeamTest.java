package com.danken.business.team;

import com.danken.business.Player;
import com.danken.business.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class addPlayerInTeamTest {


    @Test
    public void setTeamMember1IfNotFull(){
        Team team = new Team("test");
        team.addPlayerInTeam(mock(Player.class));
        Player secondPlayer = mock(Player.class);
        assertAll(() -> {
            assertTrue(team.addPlayerInTeam(secondPlayer));
            assertTrue(team.getTeamMembers().contains(secondPlayer));
        });
    }

}
