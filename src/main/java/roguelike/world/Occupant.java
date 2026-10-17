package roguelike.world;

import roguelike.Unit;

public class Occupant {
    private Unit unit;

    public Occupant() {
        unit = null;
    }

    public Unit GetUnit() { return unit; }

    public void SetUnit(Unit unit) {
        this.unit = unit;
    }
}
