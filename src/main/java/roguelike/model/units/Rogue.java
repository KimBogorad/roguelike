package roguelike.model.units;

import java.util.List;

public class Rogue extends Hero {
    private final int MAX_ENERGY = 100;
    private int energy;
    private int cost;

    public Rogue(String name, int hp_pool, int atk_pts, int def_pts, int cost) {
        super(name, hp_pool, atk_pts, def_pts);
        energy = MAX_ENERGY;
        this.cost = cost;
        ability_range = 2;
    }

    @Override
    public void GainExp(int exp) {
        int current_lvl = this.player_lvl;
        super.GainExp(exp);
        if(current_lvl < this.player_lvl) {
             energy = MAX_ENERGY;
        }
        while(current_lvl < this.player_lvl) {
            current_lvl++;
            this.atk_pts += 3*current_lvl;
        }
    }

    @Override
    public void Tick() {
        super.Tick();
        energy = Math.min(energy + 10, MAX_ENERGY);
    }

    @Override
    public String description() {
        return super.description() + String.format(" | Energy: %d/%d", energy, MAX_ENERGY);
    }

    @Override
    public boolean CastAbility(List<Enemy> validTargets, Hero player) { //Fan of Knives
        if (energy >= cost) {
            energy -= cost;
            sendMsg(this.name + " used Fan of Knives!");
            for (Enemy target : validTargets) {
                int defenseRoll = this.rand.nextInt(target.def_pts + 1);
                boolean killed = Attack(target, this.atk_pts, defenseRoll);
                if (killed) {
                    this.GainExp(target.GetExpVal());
                }
            }
        }
        else {
            sendMsg(this.name + " tried to use Fan of Knives but lacks energy (" + energy + "/" + cost + ").");
            return false;
        }
        return true; 
    }
}
