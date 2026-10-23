package roguelike.model.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.MessageCallback;
import roguelike.world.Cell;
import roguelike.world.Floor;
import roguelike.world.GameBoard;
import roguelike.world.Position;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BossTest {
    private MessageCallback dummyCallback;

    @BeforeEach
    public void setUp() {
        dummyCallback = msg -> {};
    }

    @Test
    public void testShoebodybopExactDamage() {
        // Create a boss with 50 attack
        Boss boss = new Boss('B', "Test Boss", 200, 50, 10, 100, 5, 2);
        
        // Hero with 0 defense to ensure exact damage calculation
        Warrior dummyPlayer = new Warrior("Victim", 100, 10, 0, 3);
        
        boss.setMessageCallback(dummyCallback);
        dummyPlayer.setMessageCallback(dummyCallback);

        assertTrue(dummyPlayer.description().contains("HP: 100/100"));

        boss.CastAbility(null, dummyPlayer);

        // The player should take exactly 50 damage (Boss ATK) because DEF is 0
        assertTrue(dummyPlayer.description().contains("HP: 50/100"), "Player should take exactly 50 damage (Boss ATK) because DEF is 0");
    }

    @Test
    public void testAbilityFrequencyAndCombatLoop() {
        Boss boss = new Boss('B', "Test Boss", 200, 30, 10, 100, 10, 2);
        
        // 0 DEF player
        Warrior dummyPlayer = new Warrior("Victim", 100, 10, 0, 3); 
        
        boss.setMessageCallback(dummyCallback);
        dummyPlayer.setMessageCallback(dummyCallback);

        // put distance between boss and player to ensure only atk is shoebodybop
        Cell[][] cells = new Cell[10][10];
        for(int y=0; y<10; y++) for(int x=0; x<10; x++) cells[y][x] = new Floor(new Position(x,y));
        GameBoard board = new GameBoard(cells);
        
        Occupant occBoss = new Occupant(); occBoss.SetUnit(boss);
        Occupant occPlayer = new Occupant(); occPlayer.SetUnit(dummyPlayer);
        
        board.setOccupant(new Position(0, 0), occBoss);
        board.setOccupant(new Position(5, 5), occPlayer);

        //turn 1
        boss.processTurn(board, dummyPlayer); 
        assertTrue(dummyPlayer.description().contains("HP: 100/100"), "No damage should be dealt yet");
        assertNull(board.getOccupant(new Position(0, 0)).GetUnit(), "Boss should have moved from starting position");
        
        //turn 2
        boss.processTurn(board, dummyPlayer); 
        assertTrue(dummyPlayer.description().contains("HP: 100/100"), "No damage should be dealt yet");
        
        //turn 3 - should cast ability
        Position posBeforeCast = board.getPositionOf(boss);
        boss.processTurn(board, dummyPlayer); 
        
        // check damage and movement logic
        assertTrue(dummyPlayer.description().contains("HP: 70/100"), "Player should take exactly 30 damage from the boss ability");
        
        assertEquals(posBeforeCast.GetX(), board.getPositionOf(boss).GetX(), "Boss should not move on the turn he casts his ability");
        assertEquals(posBeforeCast.GetY(), board.getPositionOf(boss).GetY(), "Boss should not move on the turn he casts his ability");
    }

    @Test
public void castAbilityIsDeterministicWithSameSeed() {
    int seed = 42;

    Hero hero1 = new Warrior("ted", 100, 10, 10, 3);
    Boss boss1 = new Boss('W', "Warden", 1_000, 100, 150, 500, 10, 1);
    Random rand1 = new Random(seed);
    boss1.setRand(rand1);

    Hero hero2 = new Warrior("ted", 100, 10, 10, 3);
    Boss boss2 = new Boss('W', "Warden", 1_000, 100, 150, 500, 10, 1);
    Random rand2 = new Random(seed);
    boss2.setRand(rand2);

    boss1.CastAbility(null, hero1);
    boss2.CastAbility(null, hero2);

    // 1. Same outcome on both sides
    assertEquals(hero1.description(), hero2.description(),
            "with the same seed, the ability should affect both heroes identically");

    // 2. The ability actually hurt the hero (the comparison above is not "nothing vs nothing")
    assertFalse(hero1.description().contains("HP: 100/100"), "Hero should have taken damage");

    // 3. Both Randoms were consumed the same amount, so the injected Random was really used
    assertEquals(rand1.nextInt(), rand2.nextInt(),
            "both Randoms should be at the same point in their sequence");
}

    @Test
    public void castAbilityDamageMatchesTheDefenseRoll() {
        int seed = 42;
        Hero hero = new Warrior("ted", 100, 10, 10, 3);
        Boss boss = new Boss('W', "Warden", 1_000, 100, 150, 500, 10, 1);
        boss.setRand(new Random(seed));

        // Predict the roll with an independent Random on the same seed
        int expectedRoll = new Random(seed).nextInt(10 + 1);   // hero DEF is 10
        int expectedHp = Math.max(0, 100 - Math.max(0, 100 - expectedRoll));  // hp - (boss ATK - roll)

        boss.CastAbility(null, hero);

        assertTrue(hero.description().contains("HP: " + expectedHp + "/100"),
                "Expected HP " + expectedHp + " but got: " + hero.description());
    }
}