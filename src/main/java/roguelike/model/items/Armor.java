package roguelike.model.items;

import java.util.Objects;

public class Armor extends Equipment {
    private final static int BASE_COST = 30;
    private final ArmorSlot armorSlot;

    public Armor(String name, int base, int growth, ArmorSlot armorSlot) {
        super(name, BASE_COST);
        addStat(Stat.DEFENSE, new StatScaling(base, growth));
        this.armorSlot = Objects.requireNonNull(armorSlot, "ArmorSlot cannot be null");
    }

    public ArmorSlot getSlot() {
        return this.armorSlot;
    }
    
}
