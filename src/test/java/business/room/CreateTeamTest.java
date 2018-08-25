package business.room;

import business.Player;
import business.Room;
import business.RoomSettings;
import business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CreateTeamTest {


    RoomSettings roomSettings;
    Player host;
    Room room;



    @BeforeEach
    public void setUp(){
       roomSettings = mock(RoomSettings.class);
       host = mock(Player.class);
       room = spy(new Room(host, roomSettings));

    }

    @Test
    public void maxTeamsTest(){
        doReturn(0).when(roomSettings).getMaxTeams();
        assertThrows(IllegalStateException.class, () -> room.createTeam("test", host));
    }

    @Test
    public void noDuplicatePlayerTest(){
        doReturn(5).when(roomSettings).getMaxTeams();
        room.createTeam("One", host);
        room.createTeam("Two", host);

        assertTrue(room.getBenchPlayers().size() == 0);

        List<Team> teamsWithMock = room
                .getTeams()
                .stream()
                .filter(team -> team.getTeamMember1() == host || team.getTeamMember2() == host)
                .collect(Collectors.toList());

        assertTrue(teamsWithMock.size() == 1);
    }

    @Test
    public void noSameTeamNameTest(){
        doReturn(2).when(roomSettings).getMaxTeams();
        room.createTeam("One", host);
        assertThrows(IllegalArgumentException.class, () -> room.createTeam("One", host));
    }

    @Test
    public void playerMustBeInRoomTest(){
        doReturn(2).when(roomSettings).getMaxTeams();
        Player player = new Player("test");
        assertThrows(IllegalStateException.class, () -> room.createTeam("One", player));
    }

}
