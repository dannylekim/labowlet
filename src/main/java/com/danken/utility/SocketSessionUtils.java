package com.danken.utility;

import java.util.Optional;

import com.danken.LabowletState;
import com.danken.business.Room;
import com.danken.sessions.GameSession;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketSessionUtils {

    private SocketSessionUtils() {}

    public static Room getRoom(final SimpMessageHeaderAccessor accessor) {
        final Optional<Room> room = Optional.ofNullable(accessor).map(SimpMessageHeaderAccessor::getSessionAttributes).map(map -> map.get("session")).map(GameSession.class::cast).map(GameSession::getCurrentRoom);

        if (room.isEmpty()) {
            throw new IllegalStateException("You cannot perform this request because you haven't joined or created a room yet!");
        }

        //Check if the room is active in the state
        boolean isRoomActive = (LabowletState.getInstance().getRoom(room.get().getRoomCode()) != null);
        if (!isRoomActive) {
            log.info("Trying to access a room call where the room is no longer active");
            throw new IllegalStateException("You cannot perform this request because the room you are in is no longer active!");
        }

        return room.get();
    }
}
