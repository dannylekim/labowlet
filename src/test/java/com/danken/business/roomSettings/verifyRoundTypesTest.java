package com.danken.business.roomSettings;

import com.danken.business.RoomSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class verifyRoundTypesTest {

    RoomSettings roomSettings;

    @BeforeEach
    public void setUp(){
        roomSettings = new RoomSettings();
    }


    @Test
    public void ifInEnum() throws Exception{
        ArrayList<String> roundTypes = new ArrayList<>();
        roundTypes.add("DESCRIBE_IT");
        roundTypes.add("SOUND_IT_OUT");
        roundTypes.add("ACT_IT_OUT");
        roundTypes.add("ONE_WORD_DESCRIPTION");

        roomSettings.setRoundTypes(roundTypes);
        roomSettings.verifyRoundTypes();
    }

    @Test
    public void ifNotInEnum(){
        ArrayList<String> roundTypes = new ArrayList<>();
        roundTypes.add("SOUND_IT_OUT");
        roundTypes.add("ACT_IT_OUT");
        roundTypes.add("ONE_WORD_DESCRIPTION");
        roundTypes.add("NOT_IN_ENUM");
        roomSettings.setRoundTypes(roundTypes);
        assertThrows(IllegalArgumentException.class, () -> roomSettings.verifyRoundTypes());
    }

    @Test
    public void nullArrayReturnError(){
        ArrayList<String> roundTypes = new ArrayList<>();
        roomSettings.setRoundTypes(roundTypes);
        assertThrows(IllegalArgumentException.class, () -> roomSettings.verifyRoundTypes());
    }
}
