package com.danken.business;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***
 *  This is a configuration class, meant to help initialize Room Object.
 *
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class RoomSettings {

    private List<String> roundTypes;

    private int maxTeams;

    private Duration roundTimeInSeconds;

    private boolean allowSkips;

    private int wordsPerPerson; //how many words can each person put into the wordbowl.


    //all the round types available
    private static List<String> allowedRoundTypes = new ArrayList<>() {
        {
            add("DESCRIBE_IT");
            add("ONE_WORD_DESCRIPTION");
            add("ACT_IT_OUT");
            add("SOUND_IT_OUT");
        }
    };

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
     * @throws IllegalArgumentException when there exists an unvalid room type
     */
    public void verifyRoundTypes() {
        if (!roundTypes.isEmpty()) {
            roundTypes.forEach(roundType -> {
                log.debug("Verifying if " + roundType + " is a valid input");
                if (!allowedRoundTypes.contains(roundType)) {
                    String msg = (roundType + " is not a valid input. It must be one of these possible" +
                            " choices: ") +
                            Arrays.toString(allowedRoundTypes.toArray());
                    log.warn(msg);
                    throw new IllegalArgumentException(msg);
                }
            });
        } else {
            log.warn("Cannot have an empty list of roundTypes. You must have at least one of these possible choices: " + Arrays.toString(allowedRoundTypes.toArray()));
            throw new IllegalArgumentException("Cannot have an empty list of roundTypes. You must have at least one of these possible choices: " + Arrays.toString(allowedRoundTypes.toArray()));
        }

    }


}
