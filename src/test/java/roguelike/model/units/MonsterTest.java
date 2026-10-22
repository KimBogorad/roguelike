package roguelike.model.units;

import org.junit.jupiter.api.Test;

import roguelike.world.Position;

import static org.junit.jupiter.api.Assertions.*;

public class MonsterTest {

    @Test
    public void testMovementPlayerCloseLogic() {
        Monster monster = new Monster('m', "Test Monster", 50, 5, 2, 10, 3);
        Position monsterPos = new Position(5, 5);
        Position playerPos = new Position(5, 3); 
        
        Position newPos = monster.Move(monsterPos, playerPos);
        
        // predict movement toward the player
        assertEquals(5, newPos.GetX());
        assertEquals(4, newPos.GetY());
    }

    @Test
    public void testSeededMovementLogic() {
        Monster monster = new Monster('m', "Test Monster", 50, 5, 2, 10, 3);
        Position monsterPos = new Position(5, 5);
        Position playerPos = new Position(10, 10); 
        
        // Seed the random number generator for reproducibility
        monster.rand.setSeed(42);
        
        // With the seed set, we can predict the random movement
        // first move:

        Position newPos = monster.Move(monsterPos, playerPos);
        
        assertEquals(4, newPos.GetX());
        assertEquals(5, newPos.GetY());

        // second move:
        newPos = monster.Move(newPos, playerPos);

        assertEquals(4, newPos.GetX());
        assertEquals(6, newPos.GetY());

        // third move:
        newPos = monster.Move(newPos, playerPos);

        assertEquals(4, newPos.GetX());
        assertEquals(7, newPos.GetY());

    }

    
}