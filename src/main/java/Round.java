import java.util.List;
import java.util.Random;

public class Round {
    private String roundName;
    private List<String> remainingWords;
    private String description;
    private int turns;
    private Random randomNumber = new Random();

    public Round(List<String> roundWords, String description, String roundName){
        this.remainingWords = roundWords;
        this.description = description;
        this.turns = 0;
        this.roundName = roundName;
    }

    public String getRandomWord(){
        int wordBowlSize = remainingWords.size();
        int randomIndex = randomNumber.nextInt(wordBowlSize - 1);
        return remainingWords.get(randomIndex);
    }

    public void removeWord(String word){
        remainingWords.remove(word);
    }

    public void increaseTurnCounter(){
        turns++;
    }

}
