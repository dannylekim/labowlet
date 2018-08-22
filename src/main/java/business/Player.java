package business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Player {
    private String name;
    private List<String> words;
    private String id;

    public Player(String name){
        this.name = name;
        this.words = new ArrayList<>();
        this.id = UUID.randomUUID().toString();
    }

    public void createWordBowl(ArrayList<String> inputWords){
        this.words = inputWords;
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }

    public String getId(){
        return id;
    }
}
