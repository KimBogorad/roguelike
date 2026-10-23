package roguelike.model.items;

import roguelike.model.units.Hero;

public abstract class Consumable extends Item {

    public Consumable(String name) {
        super(name);
    }

    @Override
    public String description() {
        return "";
    }

    public abstract void consume(Hero hero);
}
