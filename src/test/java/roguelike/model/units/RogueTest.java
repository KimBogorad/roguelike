package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RogueTest {
    private MessageCallback dummyCallback;

    @BeforeEach
    public void setUp() {
        dummyCallback = msg -> {};
    }

    @Test
    public void testEnergyAndFailedFanOfKnives() {
        Hero rogue = new Rogue("Test Rogue", 100, 10, 5, 20);
        rogue.setMessageCallback(dummyCallback);

        assertTrue(rogue.description().contains("Energy: 100/100"));
        
        // Leveling up replenishes energy so we need an enemy that will not die
        Monster tank = new Monster('t', "Tank", 1000, 0, 1000, 0, 1);
        List<Enemy> targets = new ArrayList<>();
        targets.add(tank);

        assertTrue(rogue.CastAbility(targets, rogue));
        assertTrue(rogue.description().contains("Energy: 80/100"), "Energy should decrease to 80");
        
        rogue.Tick(); 
        assertTrue(rogue.description().contains("Energy: 90/100"), "Energy should regenerate by 10 on tick");
    }

    @Test
    public void testSuccessfulFanOfKnivesAndLevelUp() {
        Hero rogue = new Rogue("Test Rogue", 100, 10, 5, 20);
        rogue.setMessageCallback(dummyCallback);

        // Create a weak monster to gain level easily
        Monster weakling = new Monster('m', "Weakling", 5, 0, 0, 50, 1);
        weakling.setMessageCallback(dummyCallback);

        List<Enemy> targets = new ArrayList<>();
        targets.add(weakling);

        // Cast - kill the target, check level up logic
        boolean castResult = rogue.CastAbility(targets, rogue);

        assertTrue(castResult, "Cast should succeed");
        assertTrue(weakling.description().contains("HP: 0/5"), "Monster should be dead");
        assertTrue(rogue.description().contains("Level: 2"), "Rogue should level up to 2");
        assertTrue(rogue.description().contains("Energy: 100/100"), "Energy should be refilled to MAX after level up");
    }
}