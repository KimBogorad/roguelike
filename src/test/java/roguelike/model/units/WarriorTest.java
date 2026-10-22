package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;


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

    @Test 
    public void testDeterminismInCastAbilityChoice() {
        Hero warr = new Warrior("Arthas", 100, 10, 10, 1);
        Monster m1 = new Monster('m', "name", 1_000, 1, 0, 50, 1);
        Monster m2 = new Monster('m', "name", 1_000, 1, 0, 50, 1);
        List<Enemy> monsters = new ArrayList<>();
        monsters.add(m1);
        monsters.add(m2);
        String desc1 = m1.description();
        String desc2 = m2.description();

        warr.rand.setSeed(42);

        warr.CastAbility(monsters, warr);

        assertEquals(desc1, m1.description(), "m1 should not have been picked - no damage taken");
        assertNotEquals(desc2, m2.description(), "m2 shouldve been hit - hp should change");
    }
}
