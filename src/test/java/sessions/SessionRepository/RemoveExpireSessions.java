package sessions.SessionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.session.Session;
import sessions.LabowletSession;
import sessions.LabowletSessionRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveExpireSessions {

    LabowletSessionRepository repo;

    @BeforeEach
    public void setup(){
       repo = new LabowletSessionRepository(new ConcurrentHashMap<>());
    }

    @Test
    public void expireOne(){
        Session expiredSession = new LabowletSession();
        expiredSession.setLastAccessedTime(Instant.now().minus(Duration.ofHours(1)));

        repo.save(expiredSession);

        assertSame(expiredSession, repo.findById(expiredSession.getId()));

        List<Session> listOfExpiredSessions = repo.removeExpiredSessions();

        assertEquals(1, listOfExpiredSessions.size());
        assertSame(expiredSession, listOfExpiredSessions.get(0));
        assertNull(repo.findById(expiredSession.getId()));
    }

    @Test
    public void noneExpiredIn10(){

        ArrayList<String> listIds = new ArrayList<>();

        for(int i = 0; i < 10; i ++) {
            Session session = new LabowletSession();
            repo.save(session);
            String sessionId = session.getId();
            assertSame(session, repo.findById(sessionId));
            listIds.add(sessionId);
        }

        List<Session> listOfExpiredSessions = repo.removeExpiredSessions();

        assertEquals(0, listOfExpiredSessions.size());
        listIds.forEach(id ->
                assertNotNull(repo.findById(id))
        );

    }

    @Test
    public void fiveExpiredIn10(){

        ArrayList<String> notExpiredIds = new ArrayList<>();

        for(int i = 0; i < 5; i ++) {
            Session session = new LabowletSession();
            repo.save(session);
            String sessionId = session.getId();
            assertSame(session, repo.findById(sessionId));
            notExpiredIds.add(sessionId);
        }

        ArrayList<Session> expiredSessions = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            Session expiredSession = new LabowletSession();
            expiredSession.setLastAccessedTime(Instant.now().minus(Duration.ofHours(i)));
            repo.save(expiredSession);
            String sessionId = expiredSession.getId();
            assertSame(expiredSession, repo.findById(sessionId));
            expiredSessions.add(expiredSession);

        }

        List<Session> listOfExpiredSessions = repo.removeExpiredSessions();

        assertEquals(5, listOfExpiredSessions.size());
        notExpiredIds.forEach(id ->
                assertNotNull(repo.findById(id))
        );
        expiredSessions.forEach(session -> {
                assertNull(repo.findById(session.getId()));
                assertTrue(listOfExpiredSessions.contains(session));
            }
        );



    }



}
