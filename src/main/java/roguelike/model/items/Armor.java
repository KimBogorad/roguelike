package roguelike.model.items;

public class Armor extends Equipment {
    private ArmorSlot slot;

    public Armor(String name, int defenseValue) {
        super(name);
        stats.put(Stat.DEFENSE, defenseValue);
    }
    
}
