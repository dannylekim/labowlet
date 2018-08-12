package sessions;

import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.concurrent.ConcurrentHashMap;


public class LabowletSessionRepository implements SessionRepository {
    private ConcurrentHashMap<String, Session>  repository;

    public LabowletSessionRepository(ConcurrentHashMap<String, Session> repository) {
        this.repository = repository;
    }

    public void removeExpiredSessions(){
        //todo
    }

    @Override
    public Session createSession() {
        //todo verify if there is already a session in the block
        return new LabowletSession();
    }

    /***
     * Saves the current session into the repository. Please note that it uses the sessionId, therefore
     * only one session can be saved. This way, the user cannot have multiple sessions linked to one "game"
     *
     * @param session
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
