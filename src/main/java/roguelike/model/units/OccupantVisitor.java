package roguelike.model.units;

public interface OccupantVisitor {
    void visit(Hero hero);
    void visit(Enemy enemy);
}