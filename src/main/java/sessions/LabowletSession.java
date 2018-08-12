package sessions;

import org.springframework.session.Session;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


/***
 *
 * //todo
 *
 *
 */
public class LabowletSession implements Session, Serializable {

    // ---------- STATIC CONSTANTS ------------------- //

    private static final Duration DEFAULT_MAX_INACTIVE_INTERVAL = Duration.ofHours(1);


    // ------------ Sessions Implementation ----------- //

    private String sessionId;
    private HashMap<String, Object> attributes; //used to store extra information
    private Instant creationTime;
    private Instant lastAccessedTime;
    private Duration maxInactiveInterval;
    private boolean isExpired; //used if there will ever be a timer/listener interaction that will set isExpired to true

    /***
     * Creates a new Labowlet Session. It will set the default settings by creating sessionId, attributes, creation
     * time, lastAccessedTime, maxInactiveInterval and isExpired.
     */
    public LabowletSession(){
        this.attributes = new HashMap<>();
        this.creationTime = Instant.now();
        this.lastAccessedTime = Instant.now();
        this.isExpired = false;
        this.maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL;
        this.sessionId = UUID.randomUUID().toString();
    }

    /***
     * Copy constructor for a Labowlet Session
     *
     * @param session
     */
    LabowletSession(LabowletSession session){
        this.attributes = new HashMap<>();
        Set<String> attributeNames = session.getAttributeNames();
        for (String attributeName: attributeNames) {
            this.attributes.put(attributeName, session.getAttribute(attributeName));
        }
        this.creationTime = session.getCreationTime();
        this.lastAccessedTime = session.getLastAccessedTime();
        this.maxInactiveInterval = session.getMaxInactiveInterval();
        this.isExpired = session.isExpired();
        this.sessionId = session.getId();
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public String changeSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    @Override
    public void removeAttribute(String attributeName) {
        attributes.remove(attributeName);
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public Instant getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public Duration getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public boolean isExpired() {
        return isExpired;
    }
}
