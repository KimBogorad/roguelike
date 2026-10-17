package roguelike.model.units;

import org.junit.jupiter.api.Test;

import roguelike.world.Position;

import static org.junit.jupiter.api.Assertions.*;

public class MonsterTest {

    @Test
    public void testMovementBasicLogic() {
        Monster monster = new Monster('m', "Test Monster", 50, 5, 2, 10, 3);
        Position monsterPos = new Position(5, 5);
        Position playerPos = new Position(5, 3); 
        
        Position newPos = monster.Move(monsterPos, playerPos);
        
        // predict movement toward the player
        assertEquals(5, newPos.GetX());
        assertEquals(4, newPos.GetY());
    }
}