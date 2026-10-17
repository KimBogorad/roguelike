package roguelike;

public interface CellVisitor {
    void visit(Wall wall);
    void visit(Floor floor);
}