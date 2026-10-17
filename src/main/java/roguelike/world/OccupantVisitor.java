package roguelike.world;

import roguelike.Enemy;
import roguelike.Hero;

public interface OccupantVisitor {
    void visit(Hero hero);
    void visit(Enemy enemy);
}