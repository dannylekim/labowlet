package business.room;

import business.Player;
import business.Room;
import business.RoomSettings;
import business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    //todo return false if somehow cannot be removed

    @Test
    public void returnTrueIfInBench() throws Exception{
        Player teamPlayer = mock(Player.class);
        Team testTeam = new Team("test", teamPlayer);
        assertTrue(room.addPlayerToTeam(testTeam, host));
        assertTrue(testTeam.getTeamMember1() == host || testTeam.getTeamMember2() == host);
        assertEquals(room.getBenchPlayers().size(), 0);
    }

    @Test
    public void returnTrueIfInTeam() throws Exception{
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

    @Test
    public void throwErrorIfPlayerNotInRoom() throws Exception{
        Player playerNotInRoom = new Player("notInRoom");
        Team testTeam = new Team("test", new Player("playerInTeam"));
        room.getTeams().add(testTeam);
        assertThrows(IllegalStateException.class, () -> room.addPlayerToTeam(testTeam, playerNotInRoom));
    }

    @Test
    public void throwErrorIfNoOpenSlot() throws Exception{
        Team fullTeam = new Team("fullTeam",  new Player("one"));
        fullTeam.addPlayerInTeam(new Player("anotherOne"));
        room.getTeams().add(fullTeam);

        assertThrows(IllegalStateException.class, () -> room.addPlayerToTeam(fullTeam, host));


    }

    @Test
    public void throwErrorIfPlayerAlreadyInTeam() throws Exception{
        Player somePlayerAlreadyInTeam = new Player("in");
        Team testTeam = new Team("test", somePlayerAlreadyInTeam);
        room.getTeams().add(testTeam);

        assertThrows(IllegalArgumentException.class,() -> room.addPlayerToTeam(testTeam, somePlayerAlreadyInTeam));
    }

    @Test
    public void throwErrorIfPlayerNotRemoved() throws Exception{
        doReturn(false).when(room).removePlayer(any());
        Player somePlayerAlreadyInTeam = new Player("in");
        Team testTeam = new Team("test", somePlayerAlreadyInTeam);
        room.getTeams().add(testTeam);

        assertThrows(Exception.class, () -> room.addPlayerToTeam(testTeam, host));
    }


}
