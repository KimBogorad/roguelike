package roguelike.model.items;

public class Weapon extends Equipment{
    
    public Weapon(String name, int attackValue) {
        super(name);
        stats.put(Stat.ATTACK, attackValue);
    }
}
