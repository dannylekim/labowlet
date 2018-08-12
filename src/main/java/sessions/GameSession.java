package sessions;

import business.Player;
import business.Room;
import business.Team;

public class GameSession {

    private Player player;
    private Room currentRoom;

    public GameSession(String name){
        this.player = new Player(name);
    }

    public boolean joinRoom(String roomCode) {
        return true;
    }

    public Player getPlayer(){
        return player;
    }

    public String getRoomId(){
        return currentRoom.getRoomCode();
    }
}
