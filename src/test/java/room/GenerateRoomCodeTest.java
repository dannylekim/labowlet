package room;

import business.Player;
import business.Room;
import business.RoomSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.spy;

public class GenerateRoomCodeTest {

    @Mock
    Player player;
    @Mock
    RoomSettings roomSettings;

    Room room;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isAlphanumerical() {
        room = spy(new Room(player, roomSettings));
        assertTrue(room.getRoomCode().matches("^[a-zA-Z0-9]+$"));
    }
}
