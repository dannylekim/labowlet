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
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Player player = new Player("test");
        assertFalse(room.addPlayerToTeam(team, player));
    }

    @Test
    public void returnTrueIfInBench(){
        Player teamPlayer = mock(Player.class);
        Team testTeam = new Team("test", teamPlayer);
        assertTrue(room.addPlayerToTeam(testTeam, host));
        assertTrue(testTeam.getTeamMember1() == host || testTeam.getTeamMember2() == host);
        assertEquals(room.getBenchPlayers().size(), 0);
    }

    @Test
    public void returnTrueIfInTeam(){
        Team hostTeam = new Team("one", host);

        Player initialTeamMember = mock(Player.class);
        Team testTeam = new Team("two", initialTeamMember);

        room.getBenchPlayers().remove(host);

        room.getTeams().add(hostTeam);
        room.getTeams().add(testTeam);

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
