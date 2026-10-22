package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testDeterminismInCastAbilityAttack() {
        Hero hero1 = new Mage("Gandalf", 100, 10, 10, 100, 10, 50, 1, 100);
        Monster m1 = new Monster('m', "monsty", 100, 10, 10, 100, 1);
        List<Enemy> monsters = new ArrayList<>();

        monsters.add(m1);
        hero1.rand.setSeed(50);

        hero1.CastAbility(monsters, hero1);

        Hero hero2 = new Mage("Gandalf", 100, 10, 10, 100, 10, 50, 1, 100);
        Monster m2 = new Monster('m', "monsty", 100, 10, 10, 100, 1);
        
        monsters.remove(0);
        monsters.add(m2);
        hero2.rand.setSeed(50);

        hero2.CastAbility(monsters, hero2);

        assertEquals(m1.description(), m2.description());
    }

    @Test 
    public void testDeterminismInCastAbilityChoice() {
        Hero mage = new Mage("Gandalf", 100, 10, 10, 1_000, 50, 100, 1, 100);
        Monster m1 = new Monster('m', "name", 1_000, 1, 0, 50, 1);
        Monster m2 = new Monster('m', "name", 1_000, 1, 0, 50, 1);
        List<Enemy> monsters = new ArrayList<>();
        monsters.add(m1);
        monsters.add(m2);
        String desc1 = m1.description();
        String desc2 = m2.description();

        mage.rand.setSeed(42);

        mage.CastAbility(monsters, mage);

        assertEquals(desc1, m1.description(), "m1 should not have been picked - no damage taken");
        assertNotEquals(desc2, m2.description(), "m2 shouldve been hit - hp should change");
    }
}