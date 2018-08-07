import org.springframework.boot.web.servlet.server.Session;

import java.util.List;

public class LabowletSession extends Session{
    private String ipAddress;
    private Player player;
    private Room currentRoom;
    private String sessionId;
    private Team currentTeam;

    public LabowletSession(){
        super();
        //todo: initialize
    }

    public boolean joinRoom(String roomCode){
        return true;
    }

    public void createTeam(Player teammate, String teamName){
        currentTeam = new Team(teamName, player, teammate);
        //todo
    }

    public void joinTeam(Team teamToJoin){
        teamToJoin.setTeamMember1(player);
        //todo: how to choose which member to join as
    }

}
