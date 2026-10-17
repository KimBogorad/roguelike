package roguelike;

public interface OccupantVisitor {
    void visit(Hero hero);
    void visit(Enemy enemy);
}