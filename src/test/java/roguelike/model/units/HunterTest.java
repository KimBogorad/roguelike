package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HunterTest {
    private MessageCallback dummyCallback;

    @BeforeEach
    public void setUp() {
        dummyCallback = msg -> {};
    }

    @Test
    public void testArrowMechanicsAndTick() {
        Hero hunter = new Hunter("Test Hunter", 100, 10, 5, 5);
        hunter.setMessageCallback(dummyCallback);
        
        //Check initial arrows and regeneration after ticks
        assertTrue(hunter.description().contains("Arrows: 10"));
        
        for (int i = 0; i < 10; i++) {
            hunter.Tick();
        }
        assertTrue(hunter.description().contains("Arrows: 11"), "Arrows regenerate every 10 ticks");
    }

    @Test
    public void testSuccessfulShootAndLevelUp() {
        Hero hunter = new Hunter("Test Hunter", 100, 10, 5, 5);
        hunter.setMessageCallback(dummyCallback);

        // Create a weak monster to gain level easily
        Monster weakling = new Monster('w', "Weakling", 5, 0, 0, 50, 1);
        weakling.setMessageCallback(dummyCallback);

        List<Enemy> targets = new ArrayList<>();
        targets.add(weakling);

        // start with 10 arrows
        // shoot 1 - 9 arrows left
        // gain lvl up from kill - +20 arrows - 29 arrows left
        boolean castResult = hunter.CastAbility(targets, hunter);

        assertTrue(castResult, "Cast should succeed");
        assertTrue(weakling.description().contains("HP: 0/5"), "Monster should be dead");
        assertTrue(hunter.description().contains("Level: 2"), "Hunter should level up to 2");
        assertTrue(hunter.description().contains("Arrows: 29"), "Arrows should be 29 after shooting 1 and gaining 20 from level up");
    }
}