package roguelike.model.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatScalingTest {

    @Test
    public void storesBaseAndGrowth() {
        StatScaling s = new StatScaling(8, 4);
        assertEquals(8, s.base());
        assertEquals(4, s.growth());
    }

    @Test
    public void zeroValuesAreAllowed() {
        assertDoesNotThrow(() -> new StatScaling(0, 0));
    }

    @Test
    public void negativeBaseIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new StatScaling(-1, 4));
    }

    @Test
    public void negativeGrowthIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new StatScaling(8, -1));
    }
}