package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

public class MageTest {
    private MessageCallback dummyCallback;

    @BeforeEach
    public void setUp() {
        dummyCallback = msg -> {};
    }

    @Test
    public void testManaRegenAndFailedAbility() {
        // Create a mage instance as a Hero
        Hero mage = new Mage("Test Mage", 100, 10, 5, 100, 30, 20, 3, 5);
        mage.setMessageCallback(dummyCallback);

        // Check initial mana, mana regeneration and failed ability cast due to insufficient mana
        assertTrue(mage.description().contains("Mana: 25/100"), "Starts with a quarter of mana pool");
        
        mage.Tick(); 
        assertTrue(mage.description().contains("Mana: 26/100"), "Mana regenerates by level (1)");
        
        assertFalse(mage.CastAbility(new ArrayList<>(), mage), "Should fail casting without enough mana");
    }

    @Test
    public void testSuccessfulBlizzardCast() {
        // Create a mage instance as a Hero (with enough mana to cast)
        Mage mage = new Mage("Test Mage", 100, 10, 5, 200, 30, 100, 3, 5);
        mage.setMessageCallback(dummyCallback);

        // Create a weak monster to be the target of the blizzard
        Monster weakMonster = new Monster('m', "Weakling", 10, 0, 0, 50, 3);
        weakMonster.setMessageCallback(dummyCallback);

        List<Enemy> targets = new ArrayList<>();
        targets.add(weakMonster);

        // Cast the Blizzard ability
        boolean castResult = mage.CastAbility(targets, mage);

        // Verify the results of the cast
        assertTrue(castResult, "CastAbility should return true when mana is sufficient");
        // Mana after level up explained: current_mana+mana_pool/4 = (50-30)+250/4 = 20+62=82 
        assertTrue(mage.description().contains("Mana: 82/250"), "Mana should decrease by the cost (30)");
        assertTrue(weakMonster.description().contains("HP: 0/10"), "Monster should be dead (0 HP)");
        assertTrue(mage.description().contains("Level: 2"), "Mage should level up from the EXP gained");
        assertTrue(targets.isEmpty(), "Dead monster should be removed from the valid targets list");
    }
    @Test
    public void castAbilityIsDeterministicWithSameSeed() {
        int seed = 50;

        Hero hero1 = new Mage("Gandalf", 100, 10, 10, 100, 10, 50, 1, 100);
        Monster m1 = new Monster('m', "monsty", 100, 10, 10, 100, 1);
        hero1.setRand(new Random(seed));
        List<Enemy> targets1 = new ArrayList<>();
        targets1.add(m1);

        Hero hero2 = new Mage("Gandalf", 100, 10, 10, 100, 10, 50, 1, 100);
        Monster m2 = new Monster('m', "monsty", 100, 10, 10, 100, 1);
        hero2.setRand(new Random(seed));
        List<Enemy> targets2 = new ArrayList<>();
        targets2.add(m2);

        hero1.CastAbility(targets1, hero1);
        hero2.CastAbility(targets2, hero2);

        assertEquals(m1.description(), m2.description(),
                "with the same seed, the spell should hit both monsters identically");
        // spell power 50 vs monster DEF 10: damage is always at least 40, so this isn't "nothing vs nothing"
        assertFalse(m1.description().contains("HP: 100/100"), "Monster should have taken damage");
    }

    @Test
    public void blizzardDamageMatchesTheDefenseRoll() {
        int seed = 50;
        Hero mage = new Mage("Gandalf", 100, 10, 10, 100, 10, 50, 1, 100);
        Monster monster = new Monster('m', "monsty", 100, 10, 10, 100, 1);
        mage.setRand(new Random(seed));
        List<Enemy> targets = new ArrayList<>();
        targets.add(monster);

        // Predict with an independent Random: first the target choice, then the defense roll
        Random predictor = new Random(seed);
        predictor.nextInt(1);                         // target choice (one target, but it still consumes a roll)
        int expectedRoll = predictor.nextInt(10 + 1); // monster DEF is 10
        int expectedHp = 100 - Math.max(0, 50 - expectedRoll);

        mage.CastAbility(targets, mage);

        assertTrue(monster.description().contains("HP: " + expectedHp + "/100"),
                "Expected HP " + expectedHp + " but got: " + monster.description());
    }

    @Test
    public void blizzardHitsTheTargetTheRandomPicks() {
        int seed = 42;
        Hero mage = new Mage("Gandalf", 100, 10, 10, 1_000, 50, 100, 1, 100);
        Monster m1 = new Monster('m', "name", 1_000, 1, 0, 50, 1);
        Monster m2 = new Monster('m', "name", 1_000, 1, 0, 50, 1);
        List<Enemy> targets = new ArrayList<>();
        targets.add(m1);
        targets.add(m2);
        mage.setRand(new Random(seed));

        int expectedIndex = new Random(seed).nextInt(2);
        Monster chosen = (expectedIndex == 0) ? m1 : m2;
        Monster other  = (expectedIndex == 0) ? m2 : m1;

        mage.CastAbility(targets, mage);

        // spell power 100 vs DEF 0: the defense roll is always 0, so the damage is exactly 100
        assertTrue(chosen.description().contains("HP: 900/1000"), "The picked target should take 100 damage");
        assertTrue(other.description().contains("HP: 1000/1000"), "The other target should be untouched");
    }

    @Test
    public void blizzardTargetChoiceDependsOnTheRandom() {
        // Across several randomly chosen seeds, both monsters must get picked at least once
        int seed = 42;
        boolean firstWasHit = false;
        boolean secondWasHit = false;
        Random rand = new Random(seed);

        for (int i = 0; i < 20; i++) {
            Hero mage = new Mage("Gandalf", 100, 10, 10, 1_000, 50, 100, 1, 100);
            Monster m1 = new Monster('m', "mob1", 1_000, 1, 0, 50, 1);
            Monster m2 = new Monster('m', "mob2", 1_000, 1, 0, 50, 1);
            List<Enemy> targets = new ArrayList<>();
            targets.add(m1);
            targets.add(m2);
            mage.setRand(new Random(rand.nextLong()));

            mage.CastAbility(targets, mage);

            if (!m1.description().contains("HP: 1000/1000")) firstWasHit = true;
            if (!m2.description().contains("HP: 1000/1000")) secondWasHit = true;
        }

        assertTrue(firstWasHit && secondWasHit, "Both targets should be reachable");
    }
}