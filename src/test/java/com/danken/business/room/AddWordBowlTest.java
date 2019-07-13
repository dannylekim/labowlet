package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import com.danken.business.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class AddWordBowlTest {

    @Mock
    Player host;

    @Mock
    RoomSettings roomSettings;

    Room room;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        room = spy(new Room(host, roomSettings));
    }



//    @Test
//    public void inputWordsIsNull() {
//        Player testPlayer = new Player();
//        List<Team> teams = room.getTeams();
//        Team testTeam = new Team("test");
//        testTeam.addPlayerInTeam(testPlayer);
//        teams.add(testTeam);
//
//        assertThrows(IllegalArgumentException.class, () -> room.addWordBowl(null, testPlayer));
//    }
//
//    @Test
//    public void tooManyWords() {
//
//        Player testPlayer = new Player();
//        List<Team> teams = room.getTeams();
//        Team testTeam = new Team("test");
//        testTeam.addPlayerInTeam(testPlayer);
//        teams.add(testTeam);
//
//        List<String> words = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            words.add(i + "");
//        }
//        assertThrows(IllegalArgumentException.class, () -> room.addWordBowl(words, testPlayer));
//
//    }
//
//    @Test
//    public void doubleEntries() {
//
//        Player testPlayer = new Player();
//        List<Team> teams = room.getTeams();
//        Team testTeam = new Team("test");
//        testTeam.addPlayerInTeam(testPlayer);
//        teams.add(testTeam);
//        List<String> words = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            words.add("test");
//        }
//        assertThrows(IllegalArgumentException.class, () -> room.addWordBowl(words, testPlayer));
//
//    }
//
//    @Test
//    public void successfulInput() {
//        doReturn(5).when(roomSettings).getWordsPerPerson();
//        List<String> words = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            words.add(i + "");
//        }
//        Player testPlayer = new Player();
//        List<Team> teams = room.getTeams();
//        Team testTeam = new Team("test");
//        testTeam.addPlayerInTeam(testPlayer);
//        teams.add(testTeam);
//        room.addWordBowl(words, testPlayer);
//        List<String> wordsSetIn = room.getWordsMadePerPlayer().get(testPlayer);
//        wordsSetIn.forEach(word ->
//                assertTrue(words.contains(word))
//        );
//    }
//
//    @Test
//    public void replacedInput() {
//        room.setLocked(true);
//        doReturn(5).when(roomSettings).getWordsPerPerson();
//        List<String> words = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            words.add(i + "");
//        }
//        Player testPlayer = new Player();
//        List<Team> teams = room.getTeams();
//        Team testTeam = new Team("test");
//        testTeam.addPlayerInTeam(testPlayer);
//        teams.add(testTeam);
//        room.addWordBowl(words, testPlayer);
//
//
//        List<String> newWords = new ArrayList<>();
//        for (int i = 5; i < 10; i++) {
//            newWords.add(i + "");
//        }
//
//        room.addWordBowl(newWords, testPlayer);
//        List<String> wordsSetIn = room.getWordsMadePerPlayer().get(testPlayer);
//        wordsSetIn.forEach(word ->
//                assertTrue(newWords.contains(word))
//        );
//
//
//    }

}
