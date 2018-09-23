package com.danken.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 *  This is a configuration class, meant to help initialize Room Object.
 *
 */
public class RoomSettings {
    private List<String> roundTypes;
    private int maxTeams;
    private Duration roundTimeInSeconds;
    private boolean allowSkips;
    private int wordsPerPerson; //how many words can each person put into the wordbowl.

    private static final Logger logger = LoggerFactory.getLogger(RoomSettings.class);

    //all the round types available
    private static List<String> roundTypeEnums = new ArrayList<>() {
        {
            add("DESCRIBE_IT");
            add("ONE_WORD_DESCRIPTION");
            add("ACT_IT_OUT");
            add("SOUND_IT_OUT");
        }
    };

    public RoomSettings() {
        this.roundTypes = new ArrayList<>();
    }


    public int getWordsPerPerson() {
        return wordsPerPerson;
    }

    public void setWordsPerPerson(int wordsPerPerson) {
        this.wordsPerPerson = wordsPerPerson;
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

    public void setRoundTypes(List<String> roundTypes) {
        this.roundTypes = roundTypes;
    }

    public boolean getAllowSkips() {
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


    /***
     * Used to verify that the round types are part of the enumeration. Used in a controller context to catch the error
     *
     *
     * @throws IllegalArgumentException
     */
    public void verifyRoundTypes() throws IllegalArgumentException{
        roundTypes.stream().forEach(roundType -> {
            logger.debug("Verifying if " + roundType + " is a valid input");
            if(!roundTypeEnums.contains(roundType)){
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append( roundType + " is not a valid input. It must be one of these possible" +
                        " choices: ");
                msgBuilder.append(Arrays.toString(roundTypeEnums.toArray()));
                String msg = msgBuilder.toString();
                logger.info(msg);
                throw new IllegalArgumentException(msg);
            }
        });
    }


}
