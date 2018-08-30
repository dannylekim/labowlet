package application;

import business.Room;
import sessions.GameSession;
import sessions.LabowletSessionRepository;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/***
 *  This is the state of the application at all times. Essentially, the in-memory database implementation for Labowlet.
 *  You can forcefully expire sessions with access to the session repository, manage rooms and manage global properties.
 *  Application state is a singleton that can be retrieved at any point since there is only one state per container.
 *  The developer needs to be conscious about manipulating the state of the application as there can be large
 *  repercussions that can affect multiple sessions.
 *
 */
public class LabowletState {

    private static LabowletState labowletState = null;
    private LabowletSessionRepository labowletSessionRepository;
    private Map<String, Room> activeRooms;
    private Map<String, List<String>> activeSockets; //todo sockets/roomId
    private Properties globalProperties; //todo pull from labowlet.properties

    private LabowletState(){
        activeRooms = new HashMap<>();
        globalProperties = new Properties();
    }

    public void setLabowletSessionRepository(LabowletSessionRepository labowletSessionRepository) {
        this.labowletSessionRepository = labowletSessionRepository;
    }

    public static LabowletState getInstance() {
        if(labowletState == null){
            labowletState = new LabowletState();
        }
        return labowletState;
    }

    public GameSession getGameSession(HttpSession session){
        return (GameSession) session.getAttribute("gameSession");
    }

    public void removeExpiredSessions(){
        //todo
    }

    private boolean isSessionExpired(){
        return false;
        //todo
    }

    public Room getRoom(String roomCode){
        return activeRooms.get(roomCode);
    }

    public boolean isRoomCodeUnique(String roomCode){
        return (getRoom(roomCode) == null);
    }

    public void addActiveRoom(Room newActiveRoom){
        activeRooms.put(newActiveRoom.getRoomCode(), newActiveRoom);
    }

    public void removeActiveRoom(Room expiredRoom){
        activeRooms.remove(expiredRoom);
    }





}
