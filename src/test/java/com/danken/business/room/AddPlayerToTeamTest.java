package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.RoomSettings;
import com.danken.business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AddPlayerToTeamTest {

    Player host;
    RoomSettings roomSettings;
    Team team;

    Room room;
    RoomProvider provider;

    @BeforeEach
    public void setUp() {
        host = mock(Player.class);
        roomSettings = mock(RoomSettings.class);
        team = mock(Team.class);
        room = spy(new Room(host, roomSettings));
        provider = new RoomProvider();
    }

    /***
     *
     * Returns true if the player joining the team can be found on the bench
     *
     * @throws Exception Exception occur when something major prevented the code from happening. Should never be
     * happening
     *
     */
    @Test
    public void returnTrueIfPlayerInBench() throws Exception {
        //creating players
        Player teamPlayer = mock(Player.class);
        Player benchPlayer = mock(Player.class);
        Team testTeam = new Team("test", teamPlayer);

        //Teams and BenchPlayers must all be added to validate through
        room.getTeams().add(testTeam);
        room.getBenchPlayers().add(benchPlayer);

        //should be successful and not throw
        provider.addPlayerToTeam(room, testTeam, benchPlayer);

        assertAll(() -> {
            assertTrue(testTeam.getTeamMember1() == benchPlayer || testTeam.getTeamMember2() == benchPlayer);
            assertEquals(room.getBenchPlayers().size(), 1); //assuming that the host is automatically added in
        });

    }

    /***
     * Can be found if the player(using host) can be found inside a team and try to be joining another team
     *
     * @throws Exception occur when something major prevented the code from happening. Should never be
     * happening
     */
    @Test
    public void returnTrueIfPlayerInTeam() throws Exception {
        Team hostTeam = new Team("one", host);

        Player initialTeamMember = mock(Player.class);
        Team testTeam = new Team("two", initialTeamMember);

        //the host should no longer be found in the bench
        room.getBenchPlayers().remove(host);

        room.getTeams().add(hostTeam);
        room.getTeams().add(testTeam);

        provider.addPlayerToTeam(room, testTeam, host);

        assertTrue(room.getTeams().size() == 2);

        assertAll(() -> {
            //the player should only be found in 1 team.
            assertTrue(room
                    .getTeams()
                    .stream()
                    .filter(team -> team.getTeamMember2() == host || team.getTeamMember1() == host)
                    .collect(toList())
                    .size() == 1);

            //the player should be found in the team that he was supposed to be added in
            assertTrue(testTeam.getTeamMember2() == host || testTeam.getTeamMember1() == host);
        });


    }


    /***
     * The player is required to be found in the room. The method must throw an error if otherwise.
     *
     * @throws Exception occur when something major prevented the code from happening. Should never be
     * happening
     */
    @Test
    public void throwErrorIfPlayerNotInRoom() throws Exception {
        Player playerNotInRoom = new Player("notInRoom");
        Team testTeam = new Team("test", new Player("playerInTeam"));
        room.getTeams().add(testTeam);
        assertThrows(IllegalStateException.class, () -> provider.addPlayerToTeam(room, testTeam, playerNotInRoom));
    }


    /**
     * Throw an error because the player is trying to join a full room
     *
     * @throws Exception occur when something major prevented the code from happening. Should never be
     *                   happening
     */
    @Test
    public void throwErrorIfNoOpenSlot() throws Exception {
        Team fullTeam = new Team("fullTeam", new Player("one"));
        fullTeam.addPlayerInTeam(new Player("anotherOne"));
        room.getTeams().add(fullTeam);
        assertThrows(IllegalStateException.class, () -> provider.addPlayerToTeam(room, fullTeam, host));


    }

    /***
     * The player is already inside this team, throw an error that he's trying to join the same room.
     *
     * fixme: should this really throw an error, or should it silently pass.
     *
     * @throws Exception occur when something major prevented the code from happening. Should never be
     * happening
     */
    @Test
    public void throwErrorIfPlayerAlreadyInTeam() throws Exception {
        Player somePlayerAlreadyInTeam = new Player("in");
        Team testTeam = new Team("test", somePlayerAlreadyInTeam);
        room.getTeams().add(testTeam);

        assertThrows(IllegalArgumentException.class, () -> provider.addPlayerToTeam(room, testTeam, somePlayerAlreadyInTeam));
    }

    /***
     *
     * If for some reason the player could not be removed, you must throw a general exception. This case should never
     * happen.
     *
     * @throws Exception occur when something major prevented the code from happening. Should never be
     * happening
     */
    @Test
    public void throwErrorIfPlayerNotRemoved() throws Exception {
        doReturn(false).when(provider).removePlayer(any(), any()); //player could not be removed for some reason
        Player somePlayerAlreadyInTeam = new Player("in");
        Team testTeam = new Team("test", somePlayerAlreadyInTeam);
        room.getTeams().add(testTeam);

        assertThrows(Exception.class, () -> provider.addPlayerToTeam(room, testTeam, host));
    }

    /***
     *  If for some reason the player could not be added after going through all the hoops, then there is another
     *  unknown issue and this should not be occuring.
     * @throws Exception occur when something major prevented the code from happening. Should never be
     * happening
     */
    @Test
    public void throwErrorIfPlayerCouldNotBeAdded() throws Exception {
        doReturn(true).when(provider).removePlayer(any(), any()); //able to remove the player

        Player somePlayerAlreadyInTeam = new Player("in");
        Team testTeam = spy(new Team("test", somePlayerAlreadyInTeam));
        doReturn(false).when(testTeam).addPlayerInTeam(any()); //but somehow could not be added to a team
        room.getTeams().add(testTeam);

        assertThrows(Exception.class, () -> provider.addPlayerToTeam(room, testTeam, host));
    }


}
