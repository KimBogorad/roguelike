package roguelike.model.units;

import java.util.List;

public class Mage extends Hero {
    private int mana_pool;
    private int current_mana;
    private int mana_cost;
    private int spell_power;
    private int hits_count;

    public Mage(String name, int hp_pool, int atk_pts, int def_pts, int mana_pool, int mana_cost, int spell_power, int hits_count, int ability_range) {
        super(name, hp_pool, atk_pts, def_pts);
        this.mana_pool = mana_pool;
        current_mana = mana_pool/4;
        this.mana_cost = mana_cost;
        this.spell_power = spell_power;
        this.hits_count = hits_count;
        this.ability_range = ability_range;
    }

    @Override
    public void GainExp(int exp) {
        int current_lvl = this.player_lvl;
        super.GainExp(exp);
        while(current_lvl < this.player_lvl) {
            current_lvl++;
            mana_pool += 25*current_lvl;
            current_mana = Math.min(current_mana + mana_pool/4, mana_pool);
            spell_power += 10*current_lvl;
        }
    }

    @Override
    public void Tick() {
        super.Tick();
        current_mana = Math.min(current_mana + this.player_lvl, mana_pool);
    }

    public String description() {
        return super.description() + String.format(" | Mana: %d/%d | Spell Power: %d", current_mana, mana_pool, spell_power);
    }

    @Override
    public boolean CastAbility(List<Enemy> validTargets, Hero player) {
        if (current_mana >= mana_cost) {
            current_mana -= mana_cost;
            sendMsg(this.name + " cast Blizzard, costing " + mana_cost + " mana.");
            int hits = 0;
            while (hits < hits_count && !validTargets.isEmpty()) {
                int randomIndex = rollWithinRange(validTargets.size()-1); //offset because rWR rolls max+1
                Enemy target = validTargets.get(randomIndex);
                int defenseRoll = rollWithinRange(target.getEffectiveDef());
                boolean killed = Attack(target, spell_power, defenseRoll); 
                if (killed) {
                    this.GainExp(target.GetExpVal());
                    validTargets.remove(randomIndex);
                }
                hits++;
            }
        }
        else {
            sendMsg(this.name + " tried to cast Blizzard but lacks mana (" + current_mana + "/" + mana_cost + ").");
            return false;
        }
        return true; 
    }
}