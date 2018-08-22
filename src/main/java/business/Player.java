package business;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
    private String name;
    private List<String> wordBowl;
    private String id;

    public Player(String name){
        this.name = name;
        this.wordBowl = new ArrayList<>();
        this.id = UUID.randomUUID().toString();
    }

    // ===================== Getters ======================= //

    public String getName() {
        return name;
    }

    public List<String> getWordBowl() {
        return wordBowl;
    }

    public String getId(){
        return id;
    }

    /***
     *  Creates a new word bowl with the input. Verifies that all the words are unique.
     *
     * @param inputWords
     */
    public void createWordBowl(List<String> inputWords){
        this.wordBowl = new ArrayList<>();
        inputWords.stream().forEach(word -> {

            //checking for uniqueness
            if(this.wordBowl.contains(word)){
                throw new IllegalArgumentException("Cannot have two of the same entries in your word bowl!");
            }
            this.wordBowl.add(word);
        });

    }


}
