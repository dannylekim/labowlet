package room;

import business.Player;
import business.Room;
import business.RoomSettings;
import business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CreateTeamTest {


    RoomSettings roomSettings;


    Player player;


    Room room;



    @BeforeEach
    public void setUp(){
       roomSettings = mock(RoomSettings.class);
       player = mock(Player.class);
       room = spy(new Room(player, roomSettings));

    }

    @Test
    public void maxTeamsTest(){
        doReturn(0).when(roomSettings).getMaxTeams();
        assertThrows(IllegalStateException.class, () -> room.createTeam("test", player));
    }

    @Test
    public void noDuplicatePlayerTest(){
        doReturn(5).when(roomSettings).getMaxTeams();
        doReturn(true).when(room).isPlayerInRoom(any());
        room.createTeam("One", player);
        room.createTeam("Two", player);

        List<Player> benchPlayersEqualToMock = room
                .getBenchPlayers()
                .stream()
                .filter(benchPlayer -> benchPlayer == player)
                .collect(Collectors.toList());
        assertTrue(benchPlayersEqualToMock.size() == 0);

        List<Team> teamsWithMock = room
                .getTeams()
                .stream()
                .filter(team -> team.getTeamMember1() == player || team.getTeamMember2() == player)
                .collect(Collectors.toList());

        assertTrue(teamsWithMock.size() == 1);
    }

    @Test
    public void noSameTeamNameTest(){
        doReturn(2).when(roomSettings).getMaxTeams();
        doReturn(true).when(room).isPlayerInRoom(any());
        room.createTeam("One", player);
        assertThrows(IllegalArgumentException.class, () -> room.createTeam("One", player));
    }

    @Test
    public void playerMustBeInRoomTest(){
        doReturn(2).when(roomSettings).getMaxTeams();
        doReturn(false).when(room).isPlayerInRoom(any());
        assertThrows(IllegalStateException.class, () -> room.createTeam("One", player));
    }

}
