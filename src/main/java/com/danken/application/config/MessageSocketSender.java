package com.danken.application.config;

import javax.inject.Inject;

import com.danken.business.Game;
import com.danken.business.LabowletError;
import com.danken.business.Room;
import com.danken.business.Scoreboard;
import com.danken.business.WordBowlInputState;
import com.danken.business.WordMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MessageSocketSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String ROOM_ENDPOINT = "/client/room/";

    private static final String PLAYER_ENDPOINT = "/client/player/";

    private static final String GAME_ENDPOINT = "/game";

    private static final String STATE_ENDPOINT = "/state";

    private static final String WORD_ENDPOINT = "/word";

    private static final String TIMER_ENDPOINT = "/timer";

    private static final String ERROR_ENDPOINT = "/error";

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

    public void sendWordStateMessage(final String roomCode, final WordBowlInputState state) {
        log.debug("Sending word state to all sockets connecting into /room/{}/state/word", roomCode);
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + roomCode + STATE_ENDPOINT + WORD_ENDPOINT, state);
    }

    public void sendWordMessage(final String roomCode, WordMessage wordMessage) {
        log.debug("Sending word to all sockets connecting into /room/{}/game/word", roomCode);
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + roomCode + GAME_ENDPOINT + WORD_ENDPOINT, wordMessage);
    }

    public void sendTimerMessage(final String roomCode, final int seconds) {
        log.debug("Sending timer to all sockets connecting into /room/{}/game/timer", roomCode);
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + roomCode + GAME_ENDPOINT + TIMER_ENDPOINT, seconds);
    }

    public void sendGameOverMessage(final String roomCode, Scoreboard scoreboard) {
        log.debug("Sending game over to all sockets connecting into /room/{}/game/over", roomCode);
        simpMessagingTemplate.convertAndSend(ROOM_ENDPOINT + roomCode + GAME_ENDPOINT + "/over", scoreboard);
    }

    public void sendErrorMessage(final String playerId, LabowletError error) {
        log.warn("Sending error message to all sockets connecting into /player/{}/error", playerId);
        simpMessagingTemplate.convertAndSend(PLAYER_ENDPOINT + playerId + ERROR_ENDPOINT, error);

    }

}
