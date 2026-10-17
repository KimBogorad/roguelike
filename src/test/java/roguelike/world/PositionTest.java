package roguelike.world;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    public void equalPositionsTest() {
        Position p1 = new Position(1,2);
        Position p2 = new Position(1,2);
        assertEquals(p1, p2, "Positions with same coordinates should be equal");
    }

    @Test
    public void differentPositionsTest() {
        Position p1 = new Position(1,2);
        Position p2 = new Position(2,2);
        assertNotEquals(p1, p2, "Positions with different x coordinates should not be equal");
        
        Position p3 = new Position(1,3);
        assertNotEquals(p1, p3, "Positions with different y coordinates should not be equal");
    }

    @Test
    public void hashCodeConsistencyTest() {
        Position p1 = new Position(1,2);
        Position p2 = new Position(1,2);
        assertEquals(p1.hashCode(), p2.hashCode(), "Equal positions should have the same hash code");
    }

    @Test
    public void nullPositionTest() {
        Position p1 = new Position(1,2);
        assertNotEquals(p1, null, "Position should not be equal to null");
    }

    @Test
    public void hashSetPositionBehaviorTest() {
        java.util.HashSet<Position> positionSet = new java.util.HashSet<>();
        Position p1 = new Position(1,2);
        Position p2 = new Position(1,2);
        positionSet.add(p1);
        assertTrue(positionSet.contains(p2), "HashSet should recognize equal positions");

        positionSet.add(p2);
        assertEquals(1, positionSet.size(), "HashSet should not allow duplicate positions");
    }
}
