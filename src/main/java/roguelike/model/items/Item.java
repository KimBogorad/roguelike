package roguelike.model.items;

public abstract class Item {
    private final String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() { 
        return this.name;
    }

    public abstract String description(); 
}