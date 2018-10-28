package com.danken.business.room;

import com.danken.business.Player;
import com.danken.business.RoomSettings;
import com.danken.business.Score;
import com.danken.business.Team;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoomService {

    public void createTeam(String teamName, Player player, Room room);
    public void addPlayerToBench(Player player, Room room);
    public Room createRoom(Player host, RoomSettings roomSettings);
    public void addPlayerToTeam(Room room, Team team, Player player) throws Exception;
    public Team getTeam(String teamId, List<Team> teams);
    public boolean removePlayer(Player player, Room room);
    public Score fetchScoreboard();
    public void addWordBowl(List<String> inputWords, Player player, Room room);
    public void updateRoom(RoomSettings roomSettings, Room room);

}
