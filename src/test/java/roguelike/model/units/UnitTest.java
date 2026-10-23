package roguelike.model.units;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class UnitTest {
    
    @Test
    public void seededRandProducesDeterministicCombat() {
        int seed = 42; // fixed seed for deterministic test

        Warrior attacker1 = new Warrior("Attacker", 100, 10, 5, 3);
        Warrior attacker2 = new Warrior("Attacker", 100, 10, 5, 3);
        Random rand1 = new Random(seed);

        Monster defender1 = new Monster('m', "Defender", 5, 5, 10, 3, 1);
        Monster defender2 = new Monster('m', "Defender", 5, 5, 10, 3, 1);
        Random rand2 = new Random(seed);

        attacker1.setRand(rand1);
        attacker2.setRand(rand2);

        String desc_before_combat = defender1.description();

        boolean result1 = attacker1.Combat(defender1);
        boolean result2 = attacker2.Combat(defender2);

        assertEquals(result1, result2, "Combat results should be the same with the same seed");
        assertEquals(defender1.description(), defender2.description(), "Defender states should be the same with the same seed");
        assertEquals(rand1.nextInt(), rand2.nextInt(), "same seed with same combat order should yield same result");
        assertNotEquals(desc_before_combat, defender1.description(), "combat should change defender's hp");
    }

    @Test
    public void killsDefenderWhenAttackIsOverwhelming() {
        // Seed 42 gives a non-zero attack roll; with ATK of a million a zero roll is a ~1-in-a-million outcome of this seed
        int seed = 42;
        Warrior attacker = new Warrior("Attacker", 100, 1_000_000, 0, 3);
        Monster defender = new Monster('m', "Defender", 1, 1, 0, 3, 1);
        attacker.setRand(new Random(seed));

        boolean killed = attacker.Combat(defender);

        assertTrue(killed, "Combat should report the kill");
        assertFalse(defender.isAlive(), "Defender should be dead");
        assertTrue(defender.description().contains("HP: 0/1"));
    }

    @Test
    public void missesWhenAttackIsZero() {
        // ATK 0 means the attack roll is always 0, so damage is always 0 whatever the seed is
        int seed = 42;
        Warrior attacker = new Warrior("Attacker", 100, 0, 0, 3);
        Monster defender = new Monster('m', "Defender", 100, 1, 0, 3, 1);
        String before = defender.description();
        attacker.setRand(new Random(seed));

        boolean killed = attacker.Combat(defender);

        assertFalse(killed, "Defender should survive");
        assertEquals(before, defender.description(), "Defender should take no damage");
    }

    @Test
    public void rollWithinRangeCoversWholeInclusiveRange() {
        int seed = 7;

        Warrior unit = new Warrior("Roller", 100, 10, 5, 3);
        unit.setRand(new Random(seed));
        int min = 0, max = 3;
        int[] counts = new int[max + 1];

        for (int i = 0; i < 5000; i++) {
            int roll = unit.rollWithinRange(min, max);
            assertTrue(roll >= min && roll <= max, "Roll out of range: " + roll);
            counts[roll]++;
        }

        for (int value = min; value <= max; value++) {
            assertTrue(counts[value] > 0, "Value never rolled: " + value);
        }
    }
}
