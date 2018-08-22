package player;

import business.Player;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class CreateWordBowlTest {

    /***
     * Test that the words are completely replaced each time the method is called.
     *
     */
    @Test
    public void createNewBowlTest(){
        Player player = new Player("test");
        List<String> wordTests = Arrays.asList("One", "Two", "Three");
        player.createWordBowl(wordTests);
        assertEquals(wordTests, player.getWordBowl());

        wordTests = Arrays.asList("Four", "Five", "Six");
        player.createWordBowl(wordTests);
        assertEquals(wordTests, player.getWordBowl());
    }

    @Test
    public void noSameWordsTest(){
        Player player = new Player("test");
        List<String> wordTests = Arrays.asList("One", "One", "One");
        assertThrows(IllegalArgumentException.class, () -> player.createWordBowl(wordTests));
    }
}
