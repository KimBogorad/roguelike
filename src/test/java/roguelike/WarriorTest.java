package roguelike;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WarriorTest {
    private MessageCallback dummyCallback;

    @BeforeEach
    public void setUp() {
        dummyCallback = msg -> {}; 
    }

    @Test
    public void testLevelUpAndCooldownMechanic() {
        // Create a Warrior instance as a Hero
        Hero warrior = new Warrior("Test Warrior", 100, 10, 5, 3);
        warrior.setMessageCallback(dummyCallback);

        // Level up
        warrior.GainExp(50); 
        assertTrue(warrior.description().contains("Level: 2"));
        
        // Cast Ability and check cooldown
        List<Enemy> targets = new ArrayList<>();
        assertTrue(warrior.CastAbility(targets, warrior), "Ability should cast successfully when cooldown is 0");

        assertTrue(warrior.description().contains("Cooldown: 4/3") || warrior.description().contains("Cooldown: 3/3"));
        assertFalse(warrior.CastAbility(targets, warrior), "Ability should fail when on cooldown");
    }
}
