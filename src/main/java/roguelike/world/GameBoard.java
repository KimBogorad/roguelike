package roguelike.world;

import java.util.IdentityHashMap;

import roguelike.model.units.Enemy;
import roguelike.model.units.Hero;
import roguelike.model.units.Occupant;
import roguelike.model.units.OccupantVisitor;
import roguelike.model.units.Unit;

public class GameBoard {
    private final IdentityHashMap<Unit, Position> unitPositions = new IdentityHashMap<>();
    private final Cell[][] board;

    public GameBoard(Cell[][] board) {
        this.board = board;

        //Initialize the Unit->Position mapping
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                Occupant occ = board[y][x].GetOccupant();
                if (occ != null && occ.GetUnit() != null) {
                    unitPositions.put(occ.GetUnit(), new Position(x, y));
                }
            }
        }
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
                //remove from map if needed, update board, then update map if needed

                Unit unit = floor.GetOccupant().GetUnit();
                // Removal from map should occur only when Cell unit position and map unit position match, to avoid removing a unit that has moved to a new position
                if(p.equals(unitPositions.get(unit))) {
                    unitPositions.remove(unit);
                }
                floor.SetOccupant(o);
                // Add to map only if the new occupant has a unit (not an empty floor)
                if (o.GetUnit() != null) {
                    unitPositions.put(o.GetUnit(), p);
                }
            }
        });
    }

    public void attemptMove(Unit movingUnit, Position targetPos) {
        Position currentPos = getPositionOf(movingUnit);
        if (currentPos.equals(targetPos)) {
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
        return unitPositions.get(unit);
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