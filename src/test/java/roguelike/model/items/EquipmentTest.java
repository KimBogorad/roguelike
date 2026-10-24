package roguelike.model.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EquipmentTest {
    private Weapon sword;
    private Armor helm;

    /** Test-only item with two stats, to check that every stat scales on upgrade. */
    private static class TwoStatItem extends Equipment {
        TwoStatItem() {
            super("Cursed Axe", 4);
            addStat(Stat.ATTACK, new StatScaling(10, 5));
            addStat(Stat.DEFENSE, new StatScaling(3, 2));
        }
    }

    @BeforeEach
    public void setUp() {
        sword = new Weapon("Sword", 8, 4);
        helm = new Armor("Helm", 2, 1, ArmorSlot.HEAD);
    }

    private void upgradeToMax(Equipment item) {
        while (!item.isMaxLevel()) {
            item.upgrade();
        }
    }

    // ---- stat values ----

    @Test
    public void newItemReturnsBaseValue() {
        assertEquals(8, sword.getStat(Stat.ATTACK));
        assertEquals(2, helm.getStat(Stat.DEFENSE));
    }

    @Test
    public void upgradeRaisesStatByGrowth() {
        assertTrue(sword.upgrade());
        assertEquals(12, sword.getStat(Stat.ATTACK));
    }

    @Test
    public void maxLevelValues() {
        upgradeToMax(sword);
        upgradeToMax(helm);
        assertEquals(44, sword.getStat(Stat.ATTACK));
        assertEquals(11, helm.getStat(Stat.DEFENSE));
    }

    @Test
    public void statTheItemDoesNotHaveIsZero() {
        assertEquals(0, sword.getStat(Stat.DEFENSE));
        assertEquals(0, helm.getStat(Stat.ATTACK));
    }

    @Test
    public void everyStatScalesOnUpgrade() {
        TwoStatItem axe = new TwoStatItem();
        axe.upgrade();
        assertEquals(15, axe.getStat(Stat.ATTACK));
        assertEquals(5, axe.getStat(Stat.DEFENSE));
    }

    // ---- level cap ----

    @Test
    public void upgradeStopsAtMaxLevel() {
        for (int i = 0; i < 9; i++) {
            assertTrue(sword.upgrade(), "Upgrade " + (i + 1) + " should succeed");
        }
        assertTrue(sword.isMaxLevel());
        assertFalse(sword.upgrade(), "Upgrade past the cap should fail");
        assertEquals(44, sword.getStat(Stat.ATTACK), "Value should not change at the cap");
    }

    @Test
    public void newItemIsNotMaxLevel() {
        assertFalse(sword.isMaxLevel());
    }

    // ---- upgrade cost ----

    @Test
    public void weaponCostMatchesTheTable() {
        assertEquals(50, sword.levelUpCost());   // 1 -> 2
        sword.upgrade();
        assertEquals(141, sword.levelUpCost());  // 2 -> 3
        sword.upgrade();
        assertEquals(260, sword.levelUpCost());  // 3 -> 4
    }

    @Test
    public void lastUpgradeCost() {
        for (int i = 0; i < 8; i++) {
            sword.upgrade();                     // now level 9
        }
        assertEquals(1350, sword.levelUpCost()); // 9 -> 10
    }

    @Test
    public void armorUsesItsOwnBaseCost() {
        assertEquals(30, helm.levelUpCost());
    }

    @Test
    public void costAlwaysIncreasesWithLevel() {
        int previous = sword.levelUpCost();
        while (sword.upgrade() && !sword.isMaxLevel()) {
            int current = sword.levelUpCost();
            assertTrue(current > previous, "Cost should grow: " + previous + " -> " + current);
            previous = current;
        }
    }

    @Test
    public void costAtMaxLevelThrows() {
        upgradeToMax(sword);
        assertThrows(IllegalStateException.class, () -> sword.levelUpCost());
    }

    // ---- armor slot ----

    @Test
    public void armorKeepsItsSlot() {
        assertEquals(ArmorSlot.HEAD, helm.getSlot());
    }

    @Test
    public void armorWithoutSlotIsRejected() {
        assertThrows(NullPointerException.class, () -> new Armor("Broken", 2, 1, null));
    }

    // ---- description ----

    @Test
    public void descriptionShowsNameLevelAndCurrentStat() {
        String text = sword.description();
        assertNotNull(text);
        assertTrue(text.contains("Sword"), text);
        assertTrue(text.contains("Lv.: 1"), text);
        assertTrue(text.contains("ATTACK: 8"), text);

        sword.upgrade();
        assertTrue(sword.description().contains("ATTACK: 12"), sword.description());
    }
}