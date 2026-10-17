package roguelike.world;

import roguelike.model.units.Occupant;

public class Floor extends Cell {
    private Occupant occupant;

    public Floor(Position pos) {
        super(pos);
        this.occupant = new Occupant();
    }

    public Occupant GetOccupant() { return occupant; }
    
    public boolean GetIsFree() { return occupant.GetUnit() == null; }

    public void SetOccupant(Occupant occupant) {
        this.occupant = occupant;
    }

    @Override
    public void accept(CellVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        if (occupant != null && occupant.GetUnit() != null) {
            return occupant.GetUnit().toString();
        }
        return ".";
    }
}