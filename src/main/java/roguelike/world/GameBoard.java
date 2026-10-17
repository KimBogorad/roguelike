package roguelike.world;

import roguelike.model.units.Enemy;
import roguelike.model.units.Hero;
import roguelike.model.units.Occupant;
import roguelike.model.units.OccupantVisitor;
import roguelike.model.units.Unit;

public class GameBoard {
    private Cell[][] board;

    public GameBoard(Cell[][] board) {
        this.board = board;
    }

    public Cell getCell(Position p) {
        return board[p.GetY()][p.GetX()];
    }

    public Occupant getOccupant(Position p) {
        return getCell(p).GetOccupant();
    }

    public void setOccupant(Position p, Occupant o) {
        getCell(p).accept(new CellVisitor() {
            @Override
            public void visit(Wall wall) {
            }
            
            @Override
            public void visit(Floor floor) {
                floor.SetOccupant(o); 
            }
        });
    }

    public void attemptMove(Unit movingUnit, Position targetPos) {
        Position currentPos = getPositionOf(movingUnit);
        if (currentPos != null && currentPos.GetX() == targetPos.GetX() && currentPos.GetY() == targetPos.GetY()) {
            return;
        }
        Cell targetCell = getCell(targetPos);
        targetCell.accept(new CellVisitor() {
            @Override
            public void visit(Wall wall) {
            }
            
            @Override
            public void visit(Floor floor) {
                handleFloorInteraction(floor, movingUnit, targetPos);
            }
        });
    }

    private void handleFloorInteraction(Floor floor, Unit movingUnit, Position targetPos) {
        if (floor.GetIsFree()) {
            Position currentPos = getPositionOf(movingUnit); 
            executeMovement(movingUnit, currentPos, targetPos);
        } else {
            Unit targetUnit = floor.GetOccupant().GetUnit();
            resolveEncounter(movingUnit, targetUnit);
        }
    }

    private void resolveEncounter(Unit attacker, Unit defender) {
        Position attackerPos = getPositionOf(attacker);
        Position defenderPos = getPositionOf(defender);

        defender.accept(new OccupantVisitor() {

            @Override
            public void visit(Hero targetHero) {
                attacker.accept(new OccupantVisitor() {
                    @Override public void visit(Hero movingHero) {}
                    @Override public void visit(Enemy movingEnemy) {
                        movingEnemy.Combat(targetHero);
                    }
                });
            }

            @Override
            public void visit(Enemy targetEnemy) {
                attacker.accept(new OccupantVisitor() {
                    @Override public void visit(Hero movingHero) {
                        boolean killed = movingHero.Combat(targetEnemy);
                        if (killed) {
                            movingHero.GainExp(targetEnemy.GetExpVal());
                            executeMovement(movingHero, attackerPos, defenderPos);
                        }
                    }
                    @Override public void visit(Enemy movingEnemy) {}
                });
            }
        });
    }

    private void executeMovement(Unit unit, Position from, Position to) {
        setOccupant(from, new Occupant());
        Occupant newOccupant = new Occupant();
        newOccupant.SetUnit(unit);
        setOccupant(to, newOccupant);
    }

    public Position getPositionOf(Unit unit) {
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                Occupant occ = board[y][x].GetOccupant();
                if (occ != null && occ.GetUnit() == unit) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                sb.append(board[y][x].toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}