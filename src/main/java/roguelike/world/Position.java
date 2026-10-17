package roguelike.world;

import java.util.Objects;

public final class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int GetX() { return x; }
    public int GetY() { return y; }

    public double Range(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override 
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof Position)) return false;
        Position otherPos = (Position) other;
        return this.x == otherPos.x && this.y == otherPos.y;
    }

    @Override 
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override 
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
