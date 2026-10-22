package roguelike.world;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.model.units.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {
    private GameBoard board;
    private Warrior player;

    @BeforeEach
    public void setUp() {
        Cell[][] cells = new Cell[3][3];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x == 1 && y == 0) {
                    cells[y][x] = new Wall(new Position(x, y));
                } else {
                    cells[y][x] = new Floor(new Position(x, y));
                }
            }
        }
        board = new GameBoard(cells);
        player = new Warrior("Test Player", 100, 10, 5, 3);
        player.setMessageCallback(msg -> {});
    }

    @Test
    public void testMoveToEmptyFloor() {
        Occupant occ = new Occupant();
        occ.SetUnit(player);
        board.setOccupant(new Position(0, 0), occ); 

        Position targetPos = new Position(0, 1); 
        board.attemptMove(player, targetPos);

        assertNull(board.getOccupant(new Position(0, 0)).GetUnit(), "Old position empty");
        assertEquals(player, board.getOccupant(targetPos).GetUnit(), "Player at new position");
    }

    @Test
    public void testMoveToWallIsBlocked() {
        Occupant occ = new Occupant();
        occ.SetUnit(player);
        board.setOccupant(new Position(0, 0), occ);

        Position targetPos = new Position(1, 0); 
        
        final boolean[] hitWall = {false};
        board.getCell(targetPos).accept(new CellVisitor() {
            @Override
            public void visit(Wall wall) { hitWall[0] = true; }
            @Override
            public void visit(Floor floor) { board.attemptMove(player, targetPos); }
        });

        assertTrue(hitWall[0], "Visitor identified wall");
        assertEquals(player, board.getOccupant(new Position(0, 0)).GetUnit(), "Player remained in place");
    }

    private Occupant occupantOf(Unit unit) {
        Occupant occ = new Occupant();
        occ.SetUnit(unit);
        return occ;
    }

    @Test
    public void placedUnitIsFound() {
        board.setOccupant(new Position(0, 0), occupantOf(player));
        assertEquals(new Position(0, 0), board.getPositionOf(player));
    }

    @Test
    public void neverPlacedUnitReturnsNull() {
        Warrior stranger = new Warrior("Stranger", 100, 10, 5, 3);
        assertNull(board.getPositionOf(stranger));
    }

    @Test
    public void movedUnitHasNewPosition() {
        board.setOccupant(new Position(0, 0), occupantOf(player));
        board.attemptMove(player, new Position(0, 1));
        assertEquals(new Position(0, 1), board.getPositionOf(player));
    }

    @Test
    public void removedUnitReturnsNull() {
        Position pos = new Position(0, 0);
        board.setOccupant(pos, occupantOf(player));
        board.setOccupant(pos, new Occupant());
        assertNull(board.getPositionOf(player));
    }

    @Test
    public void seededRandProducesDeterministicCombat() {
        Warrior attacker1 = new Warrior("Attacker", 100, 10, 5, 3);
        Warrior attacker2 = new Warrior("Attacker", 100, 10, 5, 3);

        Monster defender1 = new Monster('m', "Defender", 5, 5, 10, 3, 1);
        Monster defender2 = new Monster('m', "Defender", 5, 5, 10, 3, 1);


        Random rand = new Random(25); // fixed seed for deterministic test
        attacker1.setRand(rand);
        attacker2.setRand(rand);

        boolean result1 = attacker1.Combat(defender1);
        boolean result2 = attacker2.Combat(defender2);
        assertEquals(result1, result2, "Combat results should be the same with the same seed");
        assertEquals(defender1.description(), defender2.description(), "Defender states should be the same with the same seed");
    }

    @Test 
    public void guaranteedKillAndGuaranteedMiss() {
        Warrior attacker1 = new Warrior("Attacker", 100, 1_000_000, 0, 3);
        Warrior attacker2 = new Warrior("Attacker", 100, 10, 0, 3);
        
        Monster defender1 = new Monster('m', "Defender", 1, 1, 0, 3, 1);
        Monster defender2 = new Monster('m', "Defender", 100, 1, 1_000_000, 3, 1);

        Random rand = new Random(42); // fixed seed for deterministic test
        attacker1.setRand(rand);

        boolean result1 = attacker1.Combat(defender1);
        assertTrue(result1, "Defender should die after combat - 1 in a million chance to miss");

        String defender2_desc_before = defender2.description();
        attacker2.setRand(rand);

        boolean result2 = attacker2.Combat(defender2);
        assertFalse(result2, "Defender should survive after combat - 1 in a million chance to hit");
        assertEquals(defender2_desc_before, defender2.description(), "Defender state should be unchanged after combat");
    }

    @Test
    public void killingAnEnemyAndMovingIntoItsCellUpdatesBothUnits() {
        // Huge attack against 1 HP and 0 DEF: the kill is effectively guaranteed
        // The random seed is fixed to ensure deterministic behavior in the test
        Warrior killer = new Warrior("Killer", 100, 1_000_000, 0, 3);
        killer.setMessageCallback(msg -> {});
        Monster victim = new Monster('m', "Victim", 1, 0, 0, 0, 1);
        victim.setMessageCallback(msg -> {});

        Random rand = new Random(42); // fixed seed for deterministic test
        killer.setRand(rand);
        victim.setRand(rand);

        board.setOccupant(new Position(0, 0), occupantOf(killer));
        board.setOccupant(new Position(0, 1), occupantOf(victim));

        board.attemptMove(killer, new Position(0, 1));

        assertNull(board.getPositionOf(victim), "Dead enemy should leave the map");
        assertEquals(new Position(0, 1), board.getPositionOf(killer));
    }

    @Test
    public void staleClearDoesNotRemoveMovedUnit() {
        Position from = new Position(0, 0);
        Position to = new Position(0, 1);

        board.setOccupant(from, occupantOf(player));
        // wrong order on purpose: fill the new cell first, clear the old cell second
        board.setOccupant(to, occupantOf(player));
        board.setOccupant(from, new Occupant());

        assertEquals(to, board.getPositionOf(player), "Stale clear must not delete the new entry");
    }
}