package roguelike;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {
    private MessageCallback dummyCallback;

    @BeforeEach
    public void setUp() {
        dummyCallback = msg -> {};
    }

    @Test
    public void testSuicideMechanic() {
        Trap trap = new Trap('T', "Test Trap", 100, 10, 5, 20, 3, 3);
        Warrior dummyPlayer = new Warrior("Victim", 100, 0, 0, 3);
        
        trap.setMessageCallback(dummyCallback);
        dummyPlayer.setMessageCallback(dummyCallback);

        Position trapPos = new Position(2, 2);
        Position playerPos = new Position(2, 3); 

        trap.tickAndTrigger(dummyPlayer, trapPos, playerPos);

        assertTrue(trap.description().contains("HP: 0/100"), "Trap HP drops to 0 upon trigger");
        assertTrue(trap.isTriggered(), "Trap marked as triggered");
        assertTrue(dummyPlayer.description().contains("EXP: 20"), "Player gains trap EXP");
    }
}