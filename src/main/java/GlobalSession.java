import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GlobalSession {

    private static GlobalSession globalSession = null;
    private List<LabowletSession> activeSessions;
    private Properties globalConfigurations;

    private GlobalSession(){

    }

    public static GlobalSession getInstance(){
        if(globalSession == null){
            globalSession = new GlobalSession();
        }
        return globalSession;
    }

    public void initializeGlobalSession(){
        activeSessions = new ArrayList<>();
        globalConfigurations = new Properties();
    }

}
