package sessions;

import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.concurrent.ConcurrentHashMap;

/***
 * This class implements the Spring Session module and uses this custom implementation to handle the entire repository
 * of sessions. Spring Session envelopes and wraps the HttpServletRequest with a wrapper that is backed by this
 * implementation giving us full control of what session is being pushed, and how we handle it.
 *
 *
 */
public class LabowletSessionRepository implements SessionRepository {
    private ConcurrentHashMap<String, Session>  repository;

    public LabowletSessionRepository(ConcurrentHashMap<String, Session> repository) {
        this.repository = repository;
    }

    public void removeExpiredSessions(){

    }

    @Override
    public Session createSession() {
        return new LabowletSession();
    }

    private boolean isSessionExpired(Session session){
        return false;
        //todo
    }

    /***
     * Saves the current session into the repository. Please note that it uses the sessionId, therefore
     * only one session can be saved. This way, the user cannot have multiple sessions linked to one "game"
     *
     * @param session the session to save into the repository
     */
    @Override
    public void save(Session session) {
        String sessionId = session.getId();
        if(!repository.contains(sessionId)) {
            repository.put(session.getId(), session);
        }
    }


    @Override
    public Session findById(String id) {
        return repository.get(id);
    }

    @Override
    public void deleteById(String id) {
        repository.remove(id);
    }
}
