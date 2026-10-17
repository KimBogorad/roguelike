package roguelike.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roguelike.model.units.Occupant;
import roguelike.model.units.Warrior;

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
}