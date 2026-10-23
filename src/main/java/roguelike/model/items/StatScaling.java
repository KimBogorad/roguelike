package roguelike.model.items;

public record StatScaling(int base, int growth) {
    public StatScaling {
        if(base < 0) throw new IllegalArgumentException("Base value of equipment cannot be negative.");
        if(growth < 0) throw new IllegalArgumentException("Growth value of equipment cannot be negative.");
    }
}
