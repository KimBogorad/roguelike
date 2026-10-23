package roguelike.model.items;

public abstract class Item {
    protected String name;
    protected String description;

    public Item(String name) {
        this.name = name;
    }

    public abstract String description();
    
}
