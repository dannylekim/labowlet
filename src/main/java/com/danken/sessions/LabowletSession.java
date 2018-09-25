package com.danken.sessions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.Session;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


/***
 * This is the com.danken.application session, that is meant to be wrapping around the HttpSession. While it does not hold any new
 * data constants for the time being (though the developer is allowed to do so and extend the functionality), the
 * custom session allows the developer to extend, implement and fully customize how a session is handled by the
 * servlet container. Additionally, this session is agnostic of the com.danken.business logic so can be re-used and implemented
 * elsewhere
 *
 */
public class LabowletSession implements Session, Serializable {

    // ---------- STATIC CONSTANTS ------------------- //

    private static final Duration DEFAULT_MAX_INACTIVE_INTERVAL = Duration.ofHours(1);
    private static final Logger logger = LoggerFactory.getLogger(LabowletSession.class);


    // ------------ Sessions Implementation ----------- //

    private String sessionId;
    private HashMap<String, Object> attributes; //used to store extra information
    private Instant creationTime;
    private Instant lastAccessedTime;
    private Duration maxInactiveInterval;
    private boolean isExpired;


    // ----------------- Constructor --------------------- //

    /***
     * Creates a new Labowlet Session. It will set the default settings by creating sessionId, attributes, creation
     * time, lastAccessedTime, maxInactiveInterval and isExpired.
     */
    public LabowletSession() {
        this.attributes = new HashMap<>();
        this.creationTime = Instant.now();
        this.lastAccessedTime = Instant.now();
        this.isExpired = false;
        this.maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL;
        this.sessionId = UUID.randomUUID().toString();

        logger.debug("Session created {} at {} with {} hour(s) as max time before expiry",
                this.sessionId,
                LocalDateTime.ofInstant(this.creationTime,
                ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)),
                this.maxInactiveInterval.toHours());
    }

    /***
     * Copy constructor for a Labowlet Session used to allow concurrent connections all trying to create com.danken.sessions
     *
     * @param session The session to copy over
     */
    LabowletSession(LabowletSession session) {
        this.attributes = session.attributes;
        this.creationTime = session.creationTime;
        this.lastAccessedTime = session.lastAccessedTime;
        this.maxInactiveInterval = session.maxInactiveInterval;
        this.sessionId = session.sessionId;
        this.isExpired = session.isExpired;

        logger.debug("Session created {} at {} with {}", this.sessionId, this.creationTime.toString(), this.maxInactiveInterval.toString());

    }


    // ------------------------------- OVERRIDES ------------------------------------ //

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
