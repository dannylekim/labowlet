package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.RoomSettings;
import com.danken.business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class RemovePlayerTest {

    Room room;
    Player host;
    RoomProvider provider;

    @BeforeEach
    public void setUp(){
        host  = mock(Player.class);
        RoomSettings roomSettings = mock(RoomSettings.class);
        room = spy(new Room(host, roomSettings));
        provider = new RoomProvider();
    }


    /***
     *
     * Checks that the player has been removed, and has been removed from the bench
     *
     */
    @Test
    public void removePlayerFromBench(){
        Player benchPlayer = new Player("Bench");
        room.getBenchPlayers().add(benchPlayer);

        assertAll(() -> {
            assertTrue(provider.removePlayer(benchPlayer, room));
            assertEquals(room.getBenchPlayers().size(), 1);
            assertSame(room.getBenchPlayers().get(0), host);
        });
       
    }

    /***
     *
     * Checks that the player has been removed, and has been removed from the team
     *
     */
    @Test
    public void removePlayerFromTeam() throws Exception{
        Player teamPlayer = new Player("business/team");
        Team testTeam = new Team("test", teamPlayer);
        room.getTeams().add(testTeam);

        Player mockPlayer = mock(Player.class);
        room.getBenchPlayers().add(mockPlayer);
        provider.addPlayerToTeam(room, testTeam, mockPlayer);

        assertAll(() -> {
            assertTrue(provider.removePlayer(teamPlayer, room));
            assertNotEquals(testTeam.getTeamMember1(), teamPlayer);
            assertNotEquals(testTeam.getTeamMember2(), teamPlayer);
        });
    
    }

    @Test 
    public void removeTeamIfLastPlayer(){
        Player teamPlayer = new Player("business/team");
        Team testTeam = new Team("test", teamPlayer);
        room.getTeams().add(testTeam);

        assertAll(() -> {
            assertTrue(provider.removePlayer(teamPlayer, room));
            assertEquals("Empty Slot", room.getTeams().get(0).getTeamName());
        });
     
    }

    /***
     * Returns false if there is not the specified player in the room
     *
     */
    @Test
    public void playerNotInRoom(){
        Player playerNotInRoom = new Player("notInRoom");

        assertAll(() -> {
            assertFalse(provider.removePlayer(playerNotInRoom, room));
            assertEquals(room.getTeams().size(), 0);
            assertEquals(room.getBenchPlayers().size(), 1);
        });
      
    }

}
