package com.danken.application.state;

import com.danken.LabowletState;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.session.Session;
import com.danken.sessions.GameSession;
import com.danken.sessions.LabowletSession;
import com.danken.sessions.LabowletSessionRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class expireSessions {

    LabowletState state;
    LabowletSessionRepository repo;
    ConcurrentHashMap<String, Session> mapSessions;

    @BeforeEach
    public void setup(){
        state = LabowletState.getInstance();
        mapSessions = new ConcurrentHashMap<>();
        repo = new LabowletSessionRepository(mapSessions);
        state.setLabowletSessionRepository(repo);
    }


    @Test
    public void removeOneRoom() {
        //
        Session session = new LabowletSession();
        GameSession userGameSession = new GameSession();
        var player = new Player();
        player.setName("test");
        userGameSession.setPlayer(player);
        session.setAttribute("gameSession", userGameSession);
        session.setLastAccessedTime(Instant.now().minus(Duration.ofHours(2)));
        repo.save(session);
        //

        Room room = new Room(userGameSession.getPlayer(), mock(RoomSettings.class));
        userGameSession.setCurrentRoom(room);
        state.addActiveRoom(room);

        assertNotNull(state.getRoom(room.getRoomCode()));


        state.removeExpiredSessions();
        assertNull(state.getRoom(room.getRoomCode()));

    }

    @Test
    public void noRoomExpireSessions(){
        //
        Session session = new LabowletSession();
        GameSession userGameSession = new GameSession();
        session.setAttribute("gameSession", userGameSession);
        session.setLastAccessedTime(Instant.now().minus(Duration.ofHours(2)));
        repo.save(session);
        //

        assertNotNull(repo.findById(session.getId()));

        state.removeExpiredSessions();

        assertNull(repo.findById(session.getId()));


    }

    //Implicitly checks where the host does not get removed, ie the player session who is not expired
    @Test
    public void sessionWhereUserNotHost(){
        //Host player with a non-expired session
        Session session = new LabowletSession();
        GameSession userGameSession = new GameSession();
        Player player = new Player();
        userGameSession.setPlayer(player);
        player.setName("test");

        session.setAttribute("gameSession", userGameSession);
        session.setLastAccessedTime(Instant.now());
        repo.save(session);
        //

        //Expire Session who isn't the host of the room
        Session expiredSession = new LabowletSession();
        GameSession expireGameSession = new GameSession();
        expiredSession.setAttribute("gameSession", expireGameSession);
        expiredSession.setLastAccessedTime(Instant.now().minus(Duration.ofHours(5)));
        repo.save(expiredSession);
        //

        Room room = new Room(userGameSession.getPlayer(), mock(RoomSettings.class));
        userGameSession.setCurrentRoom(room);
        expireGameSession.setCurrentRoom(room);
        state.addActiveRoom(room);

        assertNotNull(state.getRoom(room.getRoomCode()));


        state.removeExpiredSessions();
        assertAll(() -> {
            assertSame(room, state.getRoom(room.getRoomCode()));
            assertEquals(1, mapSessions.size());
            assertNull(repo.findById(expiredSession.getId()));
        });

    }

}
