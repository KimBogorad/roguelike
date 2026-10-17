package roguelike.model.units;

import roguelike.world.GameBoard;

public abstract class Enemy extends Unit {
    protected int exp_val;
    protected char tile;

    public Enemy(char tile, String name, int hp_pool, int atk_pts, int def_pts, int exp_val) {
        super(name, hp_pool, atk_pts, def_pts);
        this.tile = tile;
        this.exp_val = exp_val;
    }

    public abstract void processTurn(GameBoard board, Hero player);

    public int GetExpVal() { 
        return exp_val; 
    }

    @Override
    public String description() {
        return String.format("Name: %s | HP: %d/%d | ATK: %d | DEF: %d",
                name, hp_current, hp_pool, atk_pts, def_pts);
    }

    @Override
    public String toString() {
        return String.valueOf(tile);
    }

    @Override
    public void accept(OccupantVisitor visitor) {
        visitor.visit(this);
    }
}