package roguelike.model.units;

import java.util.List;

import roguelike.world.GameBoard;
import roguelike.world.Position;

public class Boss extends Monster implements HeroicUnit {
    private int ability_frequency;
    private int combat_ticks;


    public Boss(char tile, String name, int hp_pool, int atk_pts, int def_pts, int exp_val, int vision_range, int ability_frequency) {
        super(tile, name, hp_pool, atk_pts, def_pts, exp_val, vision_range);
        this.ability_frequency = ability_frequency;
        this.combat_ticks = 0;
    }

    @Override
    public boolean CastAbility(List<Enemy> validTargets, Hero player) {
        sendMsg(this.name + " shoots a devastating blast at " + player.name + "!");
        int abilityDamage = this.atk_pts;
        int defenseRoll = rollWithinRange(player.def_pts);
        return Attack(player, abilityDamage, defenseRoll);
    }

   @Override
    public void processTurn(GameBoard board, Hero player) {
        Position currentPos = board.getPositionOf(this);
        Position playerPos = board.getPositionOf(player);
        if (currentPos.Range(playerPos) < GetVisionRange()) {
            if (combat_ticks == ability_frequency) {
                combat_ticks = 0;
                CastAbility(null, player);
                return;
            } else {
                combat_ticks++;
            }
        } else {
            combat_ticks = 0;
        }
        Position targetPos = super.Move(currentPos, playerPos);
        board.attemptMove(this, targetPos);
    }
}