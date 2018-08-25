package sessions;

import business.Player;
import business.Room;

/**
 *  Business Session. Currently being used as an attribute to the application session. When the application session expires
 *  so does this session and all of its traces.
 *
 */
public class GameSession {

    private Player player;
    private Room currentRoom;

    public GameSession(String name){
        this.player = new Player(name);
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public Player getPlayer(){
        return player;
    }

    public String getRoomCode(){
        return currentRoom.getRoomCode();
    }

    public Room getCurrentRoom(){
        return currentRoom;
    }
}
