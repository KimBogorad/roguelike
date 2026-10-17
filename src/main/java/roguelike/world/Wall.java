package roguelike.world;

public class Wall extends Cell {
    
    public Wall(Position pos) {
        super(pos);
    }

    @Override
    public void accept(CellVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Occupant GetOccupant() {
        return null;
    }

    @Override
    public String toString() {
        return "#";
    }
}