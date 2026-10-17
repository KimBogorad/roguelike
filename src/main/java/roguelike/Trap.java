package roguelike;

public class Trap extends Enemy {
    private int visibility_time;
    private int invisibility_time;
    private int ticks_count;
    private boolean visible;
    private boolean triggered = false;
    private static final int TRAP_RANGE = 2;

    public Trap(char tile, String name, int hp_pool, int atk_pts, int def_pts, int exp_val, int visibility_time, int invisibility_time) {
        super(tile, name, hp_pool, atk_pts, def_pts, exp_val);
        this.visibility_time = visibility_time;
        this.invisibility_time = invisibility_time;
        this.ticks_count = 0;
        this.visible = true;
    }

    public boolean isTriggered() { return triggered; }

    @Override
    public void processTurn(GameBoard board, Hero player) {
        Position currentPos = board.getPositionOf(this);
        this.tickAndTrigger(player, currentPos, board.getPositionOf(player));
    }

    public void tickAndTrigger(Hero player, Position trapPos, Position playerPos) {
        visible = ticks_count < visibility_time;
        if (ticks_count == (visibility_time + invisibility_time)) {
            ticks_count = 0;
        } else {
            ticks_count++;
        }

        if (!triggered && trapPos.Range(playerPos) < TRAP_RANGE) {
            sendMsg("You triggered " + this.name + "!");
            this.Combat(player);
            this.triggered = true;
            this.hp_current = 0;
            player.GainExp(this.exp_val);
        }
    }

    @Override
    public void Tick() {
        visible = ticks_count < visibility_time;
        if (ticks_count == (visibility_time + invisibility_time)) {
            ticks_count = 0;
        } else {
            ticks_count++;
        }
    }

    @Override
    public String toString() {
        return (visible && !triggered) ? String.valueOf(super.tile) : ".";
    }
}