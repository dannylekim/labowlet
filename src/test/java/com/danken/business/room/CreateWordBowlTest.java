package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import com.danken.business.Team;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

//fixme it feels more like testing towards implementation than behaviour. Seems more dependent on tests than anything

public class CreateWordBowlTest {

    /***
     * Throws if the player is not inside a team and tries to add words to the list
     */
    @Test
    public void throwIfPlayerNotInTeam() {
        Player host = new Player("test");
        Room room = new Room(host, mock(RoomSettings.class));
        assertThrows(IllegalStateException.class, () -> room.addWordBowl(null, mock(Player.class)));

    }

    /***
     *  If the room is not locked, then you cannot input in words
     *
     */
    @Test
    public void throwIfRoomIsNotLocked() {
        Player host = mock(Player.class);
        Room room = new Room(host, mock(RoomSettings.class));
        Player somePlayer = mock(Player.class);
        Team team = new Team("test", somePlayer);
        room.getTeams().add(team);
        assertThrows(IllegalStateException.class, () -> room.addWordBowl(null, somePlayer));
    }


    /***
     *
     * The amount of entries must be compliant towards the room settings
     *
     */
    @Test
    public void throwIfWordSizeIsNotLikeSetting(){
        Player host = mock(Player.class);
        RoomSettings roomSettings = new RoomSettings();
        roomSettings.setWordsPerPerson(2);
        Room room = new Room(host, roomSettings);
        Player somePlayer = mock(Player.class);
        Team team = new Team("test", somePlayer);
        room.getTeams().add(team);
        room.setLocked(true);
        List<String> wordTests = Arrays.asList("One", "Two", "Three");
        assertThrows(IllegalArgumentException.class, () -> room.addWordBowl(wordTests, somePlayer));
    }


    /***
     * Test that the words are completely replaced each time the method is called.
     *
     */
    @Test
    public void wordsMustBeReplaced(){
        Player host = mock(Player.class);
        RoomSettings roomSettings = new RoomSettings();
        roomSettings.setWordsPerPerson(3);
        Room room = new Room(host, roomSettings);
        Player somePlayer = mock(Player.class);
        Team team = new Team("test", somePlayer);
        room.getTeams().add(team);
        room.setLocked(true);


        List<String> wordTests = Arrays.asList("One", "Two", "Three");
        room.addWordBowl(wordTests, somePlayer);
        assertEquals(wordTests, room.getWordsMadePerPlayer().get(somePlayer));

        wordTests = Arrays.asList("Four", "Five", "Six");
        room.addWordBowl(wordTests, somePlayer);
        assertEquals(wordTests, room.getWordsMadePerPlayer().get(somePlayer));
    }

    /***
     *  No duplicate entries
     *
     */
    @Test
    public void noSameWordsTest(){
        Player host = mock(Player.class);
        RoomSettings roomSettings = new RoomSettings();
        roomSettings.setWordsPerPerson(3);
        Room room = new Room(host, roomSettings);
        Player somePlayer = mock(Player.class);
        Team team = new Team("test", somePlayer);
        room.getTeams().add(team);
        room.setLocked(true);


        List<String> wordTests = Arrays.asList("One", "One", "One");
        assertThrows(IllegalArgumentException.class, () -> room.addWordBowl(wordTests, somePlayer));
    }




}
