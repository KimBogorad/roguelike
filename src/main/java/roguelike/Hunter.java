package roguelike;

import java.util.List;

public class Hunter extends Hero{
    private int arrows;
    private int tick_count;
    private final int STARTING_ARROWS = 10;

    public Hunter(String name, int hp_pool, int atk_pts, int def_pts, int range) {
        super(name, hp_pool, atk_pts, def_pts);
        this.ability_range = range;
        arrows = STARTING_ARROWS;
        tick_count = 0;
    }

    @Override
    public void GainExp(int exp) {
        int current_lvl = this.player_lvl;
        super.GainExp(exp);
        while(current_lvl < this.player_lvl) {
            current_lvl++;
            arrows += 10*current_lvl;
            this.atk_pts += 2*current_lvl;
            def_pts += current_lvl;
        }
    }

    @Override
    public void Tick() {
        tick_count++;
        if (tick_count == 10) {
            tick_count = 0;
            arrows += this.player_lvl;
        }
    }

    @Override
    public String description() {
        return super.description() + String.format(" | Arrows: %d", arrows);
    }

    @Override
    public boolean CastAbility(List<Enemy> validTargets, Hero player) {
        if (arrows > 0) {
            arrows--;            
            if (!validTargets.isEmpty()) {
                Enemy target = validTargets.get(0);
                int defenseRoll = (int)(Math.random() * (target.def_pts + 1));
                boolean killed = Attack(target, this.atk_pts, defenseRoll);
                if (killed) {
                    this.GainExp(target.GetExpVal());
                }
            }
        }
        else {
            sendMsg(this.name + " tried to shoot, but has no arrows left!");
            return false;
        }
        return true; 
    }
}
