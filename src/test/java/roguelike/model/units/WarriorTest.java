package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;


import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

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
    public void avengersShieldPicksTheTargetTheRandomChooses() {
        int seed = 42;
        Hero warrior = new Warrior("Arthas", 100, 10, 10, 1);
        Monster m1 = new Monster('m', "first", 1_000, 1, 0, 50, 1);
        Monster m2 = new Monster('m', "second", 1_000, 1, 0, 50, 1);
        List<Enemy> targets = new ArrayList<>();
        targets.add(m1);
        targets.add(m2);
        warrior.setRand(new Random(seed));

        int expectedIndex = new Random(seed).nextInt(2);
        Monster chosen = (expectedIndex == 0) ? m1 : m2;
        Monster other  = (expectedIndex == 0) ? m2 : m1;

        warrior.CastAbility(targets, warrior);

        // damage is hp_pool / 10 = 10, with no defense roll
        assertTrue(chosen.description().contains("HP: 990/1000"), "Picked target should take 10 damage");
        assertTrue(other.description().contains("HP: 1000/1000"), "The other target should be untouched");
    }

    @Test
    public void avengersShieldTargetChoiceDependsOnTheRandom() {
        // Seeds derived from one fixed master Random, because consecutive seeds give the same first nextInt(2)
        Random master = new Random(42);
        boolean firstWasHit = false;
        boolean secondWasHit = false;

        for (int i = 0; i < 20; i++) {
            Hero warrior = new Warrior("Arthas", 100, 10, 10, 1);
            Monster m1 = new Monster('m', "first", 1_000, 1, 0, 50, 1);
            Monster m2 = new Monster('m', "second", 1_000, 1, 0, 50, 1);
            List<Enemy> targets = new ArrayList<>();
            targets.add(m1);
            targets.add(m2);
            warrior.setRand(new Random(master.nextLong()));

            warrior.CastAbility(targets, warrior);

            if (!m1.description().contains("HP: 1000/1000")) firstWasHit = true;
            if (!m2.description().contains("HP: 1000/1000")) secondWasHit = true;
        }

        assertTrue(firstWasHit && secondWasHit, "Both targets should be reachable");
    }
}
