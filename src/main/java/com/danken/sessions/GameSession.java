package com.danken.sessions;

import com.danken.business.Player;
import com.danken.business.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  Business Session. Currently being used as an attribute to the com.danken.application session. When the com.danken.application session expires
 *  so does this session and all of its traces.
 *
 */
@NoArgsConstructor
@Getter
@Setter

public class GameSession {

    private Player player;
    private Room currentRoom;



    public String getRoomCode(){
        return currentRoom.getRoomCode();
    }

}
