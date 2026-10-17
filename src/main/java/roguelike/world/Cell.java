package roguelike.world;

public abstract class Cell {
    protected Position pos;

    public Cell(Position pos) {
        this.pos = pos;
    }

    public Position GetPosition() { return pos; }
    
    public abstract void accept(CellVisitor visitor);

    public abstract Occupant GetOccupant();
}