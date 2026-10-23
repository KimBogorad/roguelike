package roguelike.model.items;

import java.util.Map;
import java.util.EnumMap;

public class Equipment extends Item {
    protected int level;
    protected int upgradeCost;
    protected Map<Stat, Integer> stats;

    public Equipment(String name) {
        super(name);
        this.stats = new EnumMap<>(Stat.class);
    }

    @Override 
    public String description() {
        return null; // TEMPORARY
    }
    
    protected int getStat(Stat stat) {
        return stats.getOrDefault(stat, 0);
    }

    protected void upgrade() {
        // TEMPORARY
    }
}
