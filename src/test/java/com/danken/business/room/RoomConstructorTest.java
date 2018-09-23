package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

public class RoomConstructorTest {

    @Mock
    Player host;

    @Mock
    RoomSettings settings;

    Room room;


    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        room = spy(new Room(host, settings));
    }

    /***
     *  The room code must be alphanumerical
     *
     */
    @Test
    public void isAlphanumerical() {
        assertTrue(room.getRoomCode().matches("^[a-zA-Z0-9]+$"));
    }

    /***
     * Room must not be in play, can not be able to start and is not locked
     *
     */
    @Test
    public void roomStateIsClean(){
        assertAll(() -> {
            assertFalse(room.isInPlay());
            assertFalse(room.canStart());
            assertFalse(room.isLocked());
        });
      
    }

    /***
     * All room related objects are instantiated
     *
     */
    @Test
    public void roomAllObjectsInstantiated(){
        assertAll(() -> {
            assertNotNull(room.getBenchPlayers());
            assertNotNull(room.getTeams());
            assertNotNull(room.getRoomCode());
            assertNotNull(room.getRoomSettings());
            assertNotNull(room.getRounds());
            assertNotNull(room.getWordsMadePerPlayer());
            assertNotNull(room.getWordBowl());
        });
   
    }


}
