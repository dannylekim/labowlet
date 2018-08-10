package business;

import java.util.List;

public class RoomSettings {
    private int rounds;
    private List<String> roundTypes;

    //all the round types available
    private static String roundType1 = "DESCRIBE_IT";
    private static String roundType2 = "ONE_WORD_DESCRIPTION";
    private static String roundType3 = "ACT_IT_OUT";
    private static String roundType4 = "SOUND_IT_OUT";

    private int maxTeams;
    private Object roundTime; //todo what's the Timer object going to be
    private boolean allowSkips;

    public RoomSettings(){

    }

    public int getRounds() {
        return rounds;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public Object getRoundTime() {
        return roundTime;
    }

    public List<String> getRoundTypes() {
        return roundTypes;
    }

    public void setAllowSkips(boolean allowSkips) {
        this.allowSkips = allowSkips;
    }

    public void setMaxTeams(int maxTeams) {
        this.maxTeams = maxTeams;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setRoundTime(Object roundTime) {
        this.roundTime = roundTime;
    }

    public boolean getAllowSkips(){
        return allowSkips;
    }
}
