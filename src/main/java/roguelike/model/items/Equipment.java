package roguelike.model.items;

import java.util.Map;
import java.util.EnumMap;

public abstract class Equipment extends Item {
    private int level;
    private final Map<Stat, StatScaling> stats;
    private static final int MAX_LEVEL = 10;
    private final int baseCost;

    public Equipment(String name, int baseCost) {
        super(name);
        this.baseCost = baseCost;
        this.level = 1;
        this.stats = new EnumMap<>(Stat.class);
    }

    @Override 
    public String description() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName()).append(" | Lv.: ").append(this.level);
        for (Stat stat : stats.keySet()) {
            int value = getStat(stat);
            if(value != 0) {
                sb.append(" | ").append(stat).append(": ").append(value);
            }
        }
        return sb.toString();
    }
    
    protected final void addStat(Stat stat, StatScaling statScaling) {
        stats.put(stat, statScaling);
    }

    public int getStat(Stat stat) {
        StatScaling statScaling = stats.get(stat);
        if(statScaling == null) return 0;
        return statScaling.base() + statScaling.growth() * (level-1);
    }

    public boolean upgrade() {
        if (!isMaxLevel()) {
            level += 1;
            return true;
        }
        return false;
    }

    public int levelUpCost() {
        if (!isMaxLevel())
            return (int)Math.round(baseCost * Math.pow(level, 1.5));
        String errMsg = //[name] is at the max level: [level]
        getName() + " is at the max level: " + this.level;
        throw new IllegalStateException(errMsg);
    }

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL; 
    }
}
