package room;

import business.Player;
import business.Room;
import business.RoomSettings;
import business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class IsPlayerInRoomTest {

    Player player;
    Room room;

    @BeforeEach
    public void setUp(){
        player = mock(Player.class);
        room = spy(new Room(player, mock(RoomSettings.class)));

    }

    @Test
    public void mockPlayerInRoom() {
        assertTrue(room.isPlayerInRoom(player));
    }

    @Test
    public void playerInBench(){
        Player testPlayer = mock(Player.class);
        room.getBenchPlayers().add(testPlayer);
        assertTrue(room.isPlayerInRoom(testPlayer));
    }

    @Test
    public void playerInTeam(){
        Player testPlayer = mock(Player.class);
        Team testTeam = new Team("test", testPlayer);
        room.getTeams().add(testTeam);
        assertTrue(room.isPlayerInRoom(testPlayer));
    }
}
