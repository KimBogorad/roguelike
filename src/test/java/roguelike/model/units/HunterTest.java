package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

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

    @Test
    public void castAbilityIsDeterministicWithSameSeed() {
        int seed = 50;

        Hero hero1 = new Hunter("hawkeye", 100, 10, 10, 100);
        Monster m1 = new Monster('m', "monsty", 100, 10, 5, 100, 1);
        hero1.setRand(new Random(seed));
        List<Enemy> targets1 = new ArrayList<>();
        targets1.add(m1);

        Hero hero2 = new Hunter("hawkeye", 100, 10, 10, 100);
        Monster m2 = new Monster('m', "monsty", 100, 10, 5, 100, 1);
        Random rand2 = new Random(seed);
        hero2.setRand(rand2);
        List<Enemy> targets2 = new ArrayList<>();
        targets2.add(m2);

        hero1.CastAbility(targets1, hero1);
        hero2.CastAbility(targets2, hero2);

        // 1. Same outcome on both sides
        assertEquals(m1.description(), m2.description(),
                "with the same seed, the shot should hit both monsters identically");

        // 2. The shot actually hurt the monster (monster DEF 5 < hunter ATK 10, so damage is always at least 5)
        assertFalse(m1.description().contains("HP: 100/100"), "Monster should have taken damage");
    }

    @Test
    public void shotDamageMatchesTheDefenseRoll() {
        int seed = 50;
        Hero hero = new Hunter("hawkeye", 100, 10, 10, 100);
        Monster monster = new Monster('m', "monsty", 100, 10, 5, 100, 1);
        hero.setRand(new Random(seed));
        List<Enemy> targets = new ArrayList<>();
        targets.add(monster);

        // Predict the roll with an independent Random on the same seed
        int expectedRoll = new Random(seed).nextInt(5 + 1);   // monster DEF is 5
        int expectedHp = 100 - Math.max(0, 10 - expectedRoll); // hunter ATK is 10

        hero.CastAbility(targets, hero);

        assertTrue(monster.description().contains("HP: " + expectedHp + "/100"),
                "Expected HP " + expectedHp + " but got: " + monster.description());
    }
}