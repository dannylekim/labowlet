import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<String> words;

    public Player(String name){
        this.name = name;
        words = new ArrayList<>();
    }

    public void addWord(String word){
        words.add(word);
    }
}
