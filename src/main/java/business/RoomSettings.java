package business;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/***
 *  This is a configuration class, meant to help initialize Room Object.
 *
 */
public class RoomSettings {
    private int rounds;
    private List<String> roundTypes;
    private int maxTeams;
    private Duration roundTimeInSeconds;
    private boolean allowSkips;
    private int wordsPerPerson; //how many words can each person put into the wordbowl.

    //all the round types available
    private static String roundType1 = "DESCRIBE_IT";
    private static String roundType2 = "ONE_WORD_DESCRIPTION";
    private static String roundType3 = "ACT_IT_OUT";
    private static String roundType4 = "SOUND_IT_OUT";

    public RoomSettings(){
        this.roundTypes = new ArrayList<>();
    }

    public int getRounds() {
        return rounds;
    }

    public int getWordsPerPerson() {
        return wordsPerPerson;
    }

    public void setWordsPerPerson(int wordsPerPerson) {
        this.wordsPerPerson = wordsPerPerson;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(int maxTeams) {
        this.maxTeams = maxTeams;
    }


    public List<String> getRoundTypes() {
        return roundTypes;
    }

    public void setRoundTypes(List<String> roundTypes){
        this.roundTypes = roundTypes;
    }

    public boolean getAllowSkips(){
        return allowSkips;
    }

    public void setAllowSkips(boolean allowSkips) {
        this.allowSkips = allowSkips;
    }


    public long getRoundTimeInSeconds() {
        return roundTimeInSeconds.toSeconds();
    }

    public void setRoundTimeInSeconds(long roundTimeInSeconds) {
        this.roundTimeInSeconds = Duration.ofSeconds(roundTimeInSeconds);
    }




}
