package com.danken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.danken.business.Room;
import com.danken.sessions.GameSession;
import com.danken.sessions.LabowletSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.Session;

/***
 *  This is the state of the com.danken.application at all times. Essentially, the in-memory database implementation for Labowlet.
 *  You can forcefully expire com.danken.sessions with access to the session repository, manage rooms and manage global properties.
 *  Application state is a singleton that can be retrieved at any point since there is only one state per container.
 *  The developer needs to be conscious about manipulating the state of the com.danken.application as there can be large
 *  repercussions that can affect multiple com.danken.sessions.
 *
 */
public class LabowletState {

    private static LabowletState labowletState = null;

    private LabowletSessionRepository labowletSessionRepository;

    private Map<String, Room> activeRooms;

    private static final Logger logger = LoggerFactory.getLogger(LabowletState.class);


    private LabowletState() {
        activeRooms = new HashMap<>();
    }

    public void setLabowletSessionRepository(LabowletSessionRepository labowletSessionRepository) {
        this.labowletSessionRepository = labowletSessionRepository;
    }

    public static LabowletState getInstance() {
        if (labowletState == null) {
            labowletState = new LabowletState();
        }
        return labowletState;
    }

    /***
     *  Remove all expired Sessions and with it all the rooms that they were hosting. This is assuming that if a player expired while still in a room
     * that the room was inactive in the first place and that all of the players there will be expired with it. There can be a potential bug where
     * players expire but the room is still active because the host is somehow still active, but then the rooms will not be able to self-clean their data.
     *
     */
    public void removeExpiredSessions() {
        //remove the expired
        logger.info("Removing expired sessions...");
        List<Session> expiredSessions = labowletSessionRepository.removeExpiredSessions();
        expiredSessions.forEach(session -> {
            GameSession userSession = session.getAttribute("gameSession");
            logger.debug("Removed expired session: {} with the player {}", session.getId(), userSession.getPlayer());
            Room currentRoom = userSession.getCurrentRoom();
            /*if the user is a host of a room and expired, we can assume that the room itself has to expire
            regardless of the other players who are inside. We can only assume that the other players are also expired and cleaned up as we cannot pull their
            game sessions to remove the currentRoom. Instead the currentRoom will simply point to a room that has no reference within the game state.
            */

            if (currentRoom != null && currentRoom.getHost() == userSession.getPlayer()) {
                logger.debug("Removing active room {}", currentRoom.getRoomCode());
                removeActiveRoom(currentRoom);
            }
        });
    }

    public Room getRoom(String roomCode) {
        return activeRooms.get(roomCode);
    }

    public boolean isRoomCodeUnique(String roomCode) {
        return (getRoom(roomCode) == null);
    }

    public void addActiveRoom(Room newActiveRoom) {
        activeRooms.put(newActiveRoom.getRoomCode(), newActiveRoom);
    }

    private void removeActiveRoom(Room room) {
        activeRooms.remove(room.getRoomCode());
    }

    public Session getSessionById(final String sessionId) {
        return Optional.ofNullable(labowletSessionRepository.findById(sessionId)).orElse(null);
    }

    public GameSession getGameSessionFromSession(final Session session) {
        return (GameSession) Optional.ofNullable(session).map(s -> s.getAttribute("scopedTarget.gameSession")).orElse(null);
    }


}
