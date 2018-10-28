package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.RoomSettings;
import com.danken.business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

public class GetTeamTest {

    @Mock
    Player host;

    @Mock
    RoomSettings settings;

    @InjectMocks
    Room room;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    /***
     *  You should be getting that team you're looking for
     *
     */
    @Test
    public void getTeamInside(){
        Team teamToBeFound = new Team("findMe", host);

        for(int i =0; i < 10; i++){
            Team randomTeam = new Team("" + i, mock(Player.class));
            room.getTeams().add(randomTeam);
        }

        room.getTeams().add(teamToBeFound);

        assertSame(room.getTeam(teamToBeFound.getTeamId()), teamToBeFound);

    }

    /***
     *  if the team isn't there, then you return null
     *
     */
    @Test
    public void returnNullIfNone(){
        Team teamToNotBeFound = new Team("returnNull", host);
        assertNull(room.getTeam(teamToNotBeFound.getTeamId()));

    }
}
