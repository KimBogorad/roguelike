package roguelike.model.units;

import roguelike.MessageCallback;
import java.util.Random;

public abstract class Unit {
    protected String name;
    protected int hp_pool;
    protected int hp_current;
    protected int atk_pts;
    protected int def_pts;
    protected MessageCallback msgCallback;
    protected Random rand = new Random();

    public Unit(String name, int hp_pool, int atk_pts, int def_pts) {
        this.name = name;
        this.hp_pool = hp_pool;
        this.hp_current = hp_pool;
        this.atk_pts = atk_pts;
        this.def_pts = def_pts;
    }

    public void setMessageCallback(MessageCallback msgCallback) {
        this.msgCallback = msgCallback;
    }
    public void setRand(Random rand) {
        this.rand = rand;
    }

    protected void sendMsg(String msg) {
        if (this.msgCallback != null) {
            this.msgCallback.send(msg);
        }
    }

    //This method defines the behavior of any unit after a Game Tick.
    public void Tick() {}

    //This method defines the behavior of any unit when it engages in combat with another unit.
    //This function returns true if the target was killed in combat, and false otherwise.
    public boolean Combat(Unit target) {
        sendMsg(this.name + " engaged in combat with " + target.name + ".");
        sendMsg(this.description());
        sendMsg(target.description());

        int atk_roll = rollWithinRange(0, this.atk_pts + 1);
        int def_roll = rollWithinRange(0, target.def_pts + 1);

        sendMsg(this.name + " rolled " + atk_roll + " attack points.");
        sendMsg(target.name + " rolled " + def_roll + " defense points.");
        return Attack(target, atk_roll, def_roll); //true == killed target
    }

    public boolean Attack(Unit target, int atk_pts, int def_pts) {
        int damage = Math.max(0, atk_pts - def_pts);
        sendMsg(this.name + " dealt " + damage + " damage to " + target.name + ".");
        target.hp_current -= damage;
        if (target.hp_current <= 0) {
            target.hp_current = 0;
            sendMsg(target.name + " died.");
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return this.hp_current > 0;
    }

    protected int rollWithinRange(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    public abstract String description();

    public abstract void accept(OccupantVisitor visitor);
}