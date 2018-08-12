package application;

import business.Room;
import sessions.PlayerSession;
import sessions.LabowletSessionRepository;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/***
 *  This is the state of the application at all times. Essentially, the in-memory database implementation for Labowlet.
 *  You can forcefully expire sessions with access to the session repository, manage rooms and manage global properties.
 *  Application state is a singleton that can be retrieved at any point since there is only one state per container.
 *  Tthe developer needs to be conscious about manipulating the state of the application as there can be large
 *  repercussions that can affect multiple sessions.
 *
 */
public class LabowletState {

    private static LabowletState labowletState = null;
    private LabowletSessionRepository labowletSessionRepository;
    private List<Room> activeRooms;
    private Properties globalProperties; //todo pull from application.properties?

    private LabowletState(){
        activeRooms = new ArrayList<>();
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

    public PlayerSession getGameSession(HttpSession session){
        return (PlayerSession) session.getAttribute("gameSession");
    }

    public void removeExpiredSessions(){
        //todo
    }

    private boolean isSessionExpired(){
        return false;
        //todo
    }





}
