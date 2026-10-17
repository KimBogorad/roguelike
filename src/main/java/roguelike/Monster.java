package roguelike;

import java.util.Random;

public class Monster extends Enemy {
    private int vision_range;

    public Monster(char tile, String name, int hp_pool, int atk_pts, int def_pts, int exp_val, int vision_range) {
        super(tile, name, hp_pool, atk_pts, def_pts, exp_val);
        this.vision_range = vision_range;
    }

    @Override
    public void processTurn(GameBoard board, Hero player) {
        Position currentPos = board.getPositionOf(this);
        Position targetPos = this.Move(currentPos, board.getPositionOf(player));
        board.attemptMove(this, targetPos);
    }

    public Position Move(Position myPos, Position playerPos) {
        double range = myPos.Range(playerPos);
        if (range < vision_range) {
            int dx = myPos.GetX() - playerPos.GetX();
            int dy = myPos.GetY() - playerPos.GetY();
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) return new Position(myPos.GetX() - 1, myPos.GetY());
                else return new Position(myPos.GetX() + 1, myPos.GetY());
            } else {
                if (dy > 0) return new Position(myPos.GetX(), myPos.GetY() - 1);
                else return new Position(myPos.GetX(), myPos.GetY() + 1);
            }
        } 
        else {
            Random rand = new Random();
            int move = rand.nextInt(5);
            return switch (move) {
                case 0 -> new Position(myPos.GetX() - 1, myPos.GetY());
                case 1 -> new Position(myPos.GetX() + 1, myPos.GetY());
                case 2 -> new Position(myPos.GetX(), myPos.GetY() - 1);
                case 3 -> new Position(myPos.GetX(), myPos.GetY() + 1);
                default -> myPos;
            };
        }
    }

    public int GetVisionRange() { 
        return vision_range; 
    }
}