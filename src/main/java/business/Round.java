package business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Round {
    private String roundName;
    private List<String> remainingWords;
    private String description;
    private int turns;
    private Random randomNumber = new Random();
    private static final Logger logger = LoggerFactory.getLogger(Round.class);

    public Round(List<String> roundWords, String description, String roundName){
        this.remainingWords = roundWords;
        this.description = description;
        this.turns = 0;
        this.roundName = roundName;

        logger.info("Created a round with " + Arrays.toString(roundWords.toArray()) + " and roundName "
        + this.roundName + " with " + description);

    }

    public String getRandomWord(){
        int wordBowlSize = remainingWords.size();
        int randomIndex = randomNumber.nextInt(wordBowlSize - 1);
        String randomWord = remainingWords.get(randomIndex);
        logger.info("Random word is " + randomWord);
        return randomWord;
    }

    public void removeWord(String word){
        remainingWords.remove(word);
    }

    public void increaseTurnCounter(){
        turns++;
    }

}
