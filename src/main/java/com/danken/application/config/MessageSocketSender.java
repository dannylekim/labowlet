package com.danken.application.config;

import javax.inject.Inject;

import com.danken.business.Game;
import com.danken.business.Room;
import com.danken.business.WordBowlInputState;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MessageSocketSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String ROOM_ENDPOINT = "/room/";

    private static final String GAME_ENDPOINT = "/game";

    private static final String STATE_ENDPOINT = "/state";

    @Inject
    public MessageSocketSender(final SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendRoomMessage(final Room room) {
        log.debug("Sending room to all sockets connecting into /room/{}", room.getRoomCode());
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + room.getRoomCode(), room);
    }

    public void sendGameMessage(final String roomCode, final Game game) {
        log.debug("Sending game to all sockets connecting into /room/{}/game", roomCode);
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + roomCode + GAME_ENDPOINT, game);
    }

    public void sendStateMessage(final String roomCode, final WordBowlInputState state) {
        log.debug("Sending state to all sockets connecting into /room/{}/state", roomCode);
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + roomCode + STATE_ENDPOINT, state);

    }

}
