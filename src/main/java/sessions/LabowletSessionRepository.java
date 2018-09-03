package sessions;

import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/***
 * This class implements the Spring Session module and uses this custom implementation to handle the entire repository
 * of sessions. Spring Session envelopes and wraps the HttpServletRequest with a wrapper that is backed by this
 * implementation giving us full control of what session is being pushed, and how we handle it.
 *
 *
 */
public class LabowletSessionRepository implements SessionRepository<Session> {
    private ConcurrentHashMap<String, Session>  repository;

    public LabowletSessionRepository(ConcurrentHashMap<String, Session> repository) {
        this.repository = repository;
    }

    /***
     * 
     * Goes through all of the sessions and remove those that are expired.
     * 
     * @return a List<Session> that holds all of the sessions that have been removed from the repository. 
     * 
     */
    public List<Session> removeExpiredSessions(){

        List<Session> expiredSessions = new ArrayList<>();

        //uses a stream to concurrently remove items from the map without causing issues
        repository.entrySet().removeIf(entrySet -> {
            Session session = entrySet.getValue();
            if(isSessionExpired(session)){
                expiredSessions.add(session); //if it is expired, add it to the list and return true to remove from the map
                return true;
            }
            return false;
        });

        return expiredSessions;
    }

    @Override
    public Session createSession() {
        return new LabowletSession();
    }

    /***
     * Returns the boolean value if the session is expired
     * 
     * @param session The Session that is to be checked on 
     * @return true if the currentTime is beyond the addition of lastAccessedTime + maxInactiveInterval
     */
    private boolean isSessionExpired(Session session){

        //Last accessed Time
        Instant lastAccessedTime = session.getLastAccessedTime();
        //What is the max inactive Interval and add it to the last accessed time 
        Duration maxInactiveInterval = session.getMaxInactiveInterval();


        LocalDateTime expiryDateTime = LocalDateTime.from(lastAccessedTime.plus(maxInactiveInterval));
        //check current time. If current time is passed the last access time then this session should be expired.
        LocalDateTime currentTime = LocalDateTime.now();


        return (currentTime.compareTo(expiryDateTime) > 0);
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
