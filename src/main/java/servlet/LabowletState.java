package servlet;

import business.Room;
import sessions.LabowletSessionRepository;

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
}
