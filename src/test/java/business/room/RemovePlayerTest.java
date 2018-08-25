package business.room;

import business.Player;
import business.Room;
import business.RoomSettings;
import business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class RemovePlayerTest {

    Room room;
    Player host;

    @BeforeEach
    public void setUp(){
        host  = mock(Player.class);
        RoomSettings roomSettings = mock(RoomSettings.class);
        room = spy(new Room(host, roomSettings));
    }



    @Test
    public void removePlayerFromBench(){
        Player benchPlayer = new Player("Bench");
        room.getBenchPlayers().add(benchPlayer);

        assertTrue(room.removePlayer(benchPlayer));
        assertEquals(room.getBenchPlayers().size(), 1);
        assertSame(room.getBenchPlayers().get(0), host);
    }

    @Test
    public void removePlayerFromTeam(){
        Player teamPlayer = new Player("business/team");
        Team testTeam = new Team("test", teamPlayer);
        room.getTeams().add(testTeam);

        assertTrue(room.removePlayer(teamPlayer));
        assertNull(testTeam.getTeamMember1());
        assertNull(testTeam.getTeamMember2());
    }

    @Test
    public void playerNotInRoom(){
        Player playerNotInRoom = new Player("notInRoom");
        assertFalse(room.removePlayer(playerNotInRoom));
        assertEquals(room.getTeams().size(), 0);
        assertEquals(room.getBenchPlayers().size(), 1);
    }

}
