package servlet;

import business.Room;
import org.springframework.session.Session;
import org.springframework.session.web.http.SessionRepositoryFilter;
import sessions.GameSession;
import sessions.LabowletSession;
import sessions.LabowletSessionRepository;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LabowletState {

    private static LabowletState labowletState = null;
    private LabowletSessionRepository labowletSessionRepository;
    private List<Room> activeRooms;
    private Properties globalProperties;

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





}
