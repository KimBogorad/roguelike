package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

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

    @Test
    public void fanOfKnivesIsDeterministicWithSameSeed() {
        int seed = 50;

        Hero rogue1 = new Rogue("Arya", 100, 10, 10, 10);
        Monster m1 = new Monster('m', "monsty", 100, 10, 5, 100, 1);
        rogue1.setRand(new Random(seed));
        List<Enemy> targets1 = new ArrayList<>();
        targets1.add(m1);

        Hero rogue2 = new Rogue("Arya", 100, 10, 10, 10);
        Monster m2 = new Monster('m', "monsty", 100, 10, 5, 100, 1);
        rogue2.setRand(new Random(seed));
        List<Enemy> targets2 = new ArrayList<>();
        targets2.add(m2);

        rogue1.CastAbility(targets1, rogue1);
        rogue2.CastAbility(targets2, rogue2);

        assertEquals(m1.description(), m2.description());
        // monster DEF 5 < rogue ATK 10, so the damage is always at least 5
        assertFalse(m1.description().contains("HP: 100/100"), "Monster should have taken damage");
    }

    @Test
    public void fanOfKnivesRollsDefenseForEachTargetInOrder() {
        int seed = 50;
        Hero rogue = new Rogue("Arya", 100, 10, 10, 10);
        Monster m1 = new Monster('m', "first", 100, 10, 5, 100, 1);
        Monster m2 = new Monster('m', "second", 100, 10, 5, 100, 1);
        rogue.setRand(new Random(seed));
        List<Enemy> targets = new ArrayList<>();
        targets.add(m1);
        targets.add(m2);

        // Predict with an independent Random: one defense roll per target, in list order
        Random predictor = new Random(seed);
        int hp1 = 100 - Math.max(0, 10 - predictor.nextInt(5 + 1));
        int hp2 = 100 - Math.max(0, 10 - predictor.nextInt(5 + 1));

        rogue.CastAbility(targets, rogue);

        assertTrue(m1.description().contains("HP: " + hp1 + "/100"), m1.description());
        assertTrue(m2.description().contains("HP: " + hp2 + "/100"), m2.description());
    }
}