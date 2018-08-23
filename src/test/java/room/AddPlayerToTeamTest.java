package room;

import business.Player;
import business.Room;
import business.RoomSettings;
import business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AddPlayerToTeamTest {

    Player host;
    RoomSettings roomSettings;
    Team team;

    Room room;

    @BeforeEach
    public void setUp(){
        host = mock(Player.class);
        roomSettings = mock(RoomSettings.class);
        team = mock(Team.class);
        room = spy(new Room(host, roomSettings));
    }

    @Test
    public void returnFalseIfPlayerNotIn(){
        doReturn(false).when(room).isPlayerInRoom(any());
        assertFalse(room.addPlayerToTeam(team, host));
        assertTrue(team.getTeamMember1() != host && team.getTeamMember2() != host);
    }

    @Test
    public void returnTrueIfInBench(){
        Player teamPlayer = mock(Player.class);
        Team testTeam = new Team("test", teamPlayer);
        assertTrue(room.addPlayerToTeam(testTeam, host));
        assertTrue(testTeam.getTeamMember1() == host || testTeam.getTeamMember2() == host);
        assertTrue(room.getBenchPlayers().size() == 0);
    }

    @Test
    public void returnTrueIfInTeam(){
        doCallRealMethod().when(team).setTeamMember1(any());
        doCallRealMethod().when(team).getTeamMember1();
        doCallRealMethod().when(team).getTeamMember2();

        Player initialTeamMember = mock(Player.class);
        Team testTeam = new Team("test", initialTeamMember);

        team.setTeamMember1(host);
        room.getBenchPlayers().remove(host);

        room.getTeams().add(testTeam);
        room.getTeams().add(team);

        assertTrue(room.addPlayerToTeam(testTeam, host));
        assertTrue(room.getTeams().size() == 2);
        assertTrue(room
                .getTeams()
                .stream()
                .filter(team -> team.getTeamMember2() == host || team.getTeamMember1() == host)
                .collect(toList())
                .size() == 1);
    }

}
