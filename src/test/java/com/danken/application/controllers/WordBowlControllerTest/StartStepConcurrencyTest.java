package com.danken.application.controllers.WordBowlControllerTest;

import com.danken.LabowletState;
import com.danken.application.config.MessageSocketSender;
import com.danken.application.controllers.WordBowlController;
import com.danken.business.Game;
import com.danken.business.Player;
import com.danken.business.Room;
import com.danken.business.RoomSettings;
import com.danken.business.Round;
import com.danken.business.Team;
import com.danken.business.WordMessage;
import com.danken.sessions.GameSession;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StartStepConcurrencyTest {

    @Test
    void startStep_runs_only_once_when_called_concurrently() throws Exception {
        // Arrange a minimal game setup
        final Player actor = new Player();
        actor.setName("actor");
        final Player guesser = new Player();
        guesser.setName("guesser");

        Team team = new Team("team");
        team.addPlayerInTeam(actor);
        team.addPlayerInTeam(guesser);
        List<Team> teams = List.of(team);

        Round round = constructRound("ROUND");
        setRemainingWords(round, new ArrayList<>(List.of("alpha", "beta")));
        List<Round> rounds = List.of(round);

        Game game = constructGame(teams, rounds, 1);
        game.setCurrentActor(actor);
        game.setCurrentGuesser(guesser);
        game.setTimeRemaining(1);
        game.setTurnStarted(false);

        RoomSettings settings = new RoomSettings();
        settings.setRoundTypes(List.of("DESCRIBE_IT"));
        settings.setRoundTimeInSeconds(1);
        settings.setMaxTeams(2);
        settings.setWordsPerPerson(1);
        settings.setAllowSkips(true);

        Room room = new Room(actor, settings);
        room.setGame(game);
        LabowletState.getInstance().addActiveRoom(room);

        GameSession session = new GameSession();
        session.setPlayer(actor);
        session.setCurrentRoom(room);

        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        HashMap<String, Object> sessionAttrs = new HashMap<>();
        sessionAttrs.put("session", session);
        accessor.setSessionAttributes(sessionAttrs);

        MessageSocketSender sender = mock(MessageSocketSender.class);
        WordBowlController controller = new WordBowlController(sender);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch go = new CountDownLatch(1);

        Runnable startCall = () -> {
            try {
                go.await();
                controller.startStep(accessor);
            } catch (Exception ignored) {
            }
        };

        pool.submit(startCall);
        pool.submit(startCall);
        go.countDown();
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        // Assert: only one word was sent and turn is started
        verify(sender, times(1)).sendWordMessage(eq(room.getRoomCode()), any(WordMessage.class));
        assertTrue(game.isTurnStarted());
    }

    private static Game constructGame(List<Team> teams, List<Round> rounds, int wordsPerPerson) throws Exception {
        Constructor<Game> ctor = Game.class.getDeclaredConstructor(List.class, List.class, int.class);
        ctor.setAccessible(true);
        return ctor.newInstance(teams, rounds, wordsPerPerson);
    }

    private static Round constructRound(String name) throws Exception {
        Constructor<Round> ctor = Round.class.getDeclaredConstructor(String.class);
        ctor.setAccessible(true);
        return ctor.newInstance(name);
    }

    private static void setRemainingWords(Round round, List<String> words) throws Exception {
        var field = Round.class.getDeclaredField("remainingWords");
        field.setAccessible(true);
        field.set(round, words);
    }
}
