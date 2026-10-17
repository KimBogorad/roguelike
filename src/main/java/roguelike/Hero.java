package roguelike;

import roguelike.world.OccupantVisitor;

public abstract class Hero extends Unit implements HeroicUnit  {
    protected int exp;
    protected int player_lvl;
    protected int ability_range;
    
    public Hero(String name, int hp_pool, int atk_pts, int def_pts) {
        super(name, hp_pool, atk_pts, def_pts);
        this.exp = 0;
        this.player_lvl = 1;
    } 

    @Override
    public void Tick() {
        // request input and move accordingly. If the hero is in range of an enemy, attack it.
    }

    public void GainExp(int exp) {
        this.exp += exp;
        while (this.exp >= 50*player_lvl) {
            this.exp -= 50*player_lvl;
            player_lvl++;
            this.hp_pool += 10*player_lvl;
            this.hp_current = this.hp_pool;
            this.atk_pts += 4*player_lvl;
            this.def_pts += player_lvl;
        }
    }

    @Override
    public String description() {
        return String.format("Name: %s | HP: %d/%d | ATK: %d | DEF: %d | Level: %d | EXP: %d",
                name, hp_current, hp_pool, atk_pts, def_pts, player_lvl, exp);
    }
    
    @Override
    public String toString() {
        if(this.hp_current > 0) {
            return "@";
        }
        return "X";
    }
    
    @Override
    public void accept(OccupantVisitor visitor) {
        visitor.visit(this);
    }
}