package roguelike.model.items;

public class Weapon extends Equipment{
    private final static int BASE_COST = 50;
    
    public Weapon(String name, int base, int growth) {
        super(name, BASE_COST);
        addStat(Stat.ATTACK, new StatScaling(base, growth));
    }
}
