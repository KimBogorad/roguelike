package roguelike;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import roguelike.model.units.Boss;
import roguelike.model.units.Enemy;
import roguelike.model.units.Hero;
import roguelike.model.units.Hunter;
import roguelike.model.units.Mage;
import roguelike.model.units.Monster;
import roguelike.model.units.Occupant;
import roguelike.model.units.Rogue;
import roguelike.model.units.Trap;
import roguelike.model.units.Unit;
import roguelike.model.units.Warrior;
import roguelike.world.Cell;
import roguelike.world.CellVisitor;
import roguelike.world.Floor;
import roguelike.world.GameBoard;
import roguelike.world.Position;
import roguelike.world.Wall;

public class GameManager {

    private static final int MAX_LEVELS = 4;
    private static final String LEVEL_FILE_PREFIX = "level";
    private static final String LEVEL_FILE_SUFFIX = ".txt";

    private GameBoard board;
    private Hero player;
    private List<Enemy> enemies;
    private final Scanner scanner;
    private final MessageCallback out;
    private int currentLevel;
    private String levelsDir;

    /** Inject a custom output sink. */
    public GameManager(String path) {
        this.out = System.out::println;
        this.scanner = new Scanner(System.in);
        this.enemies = new ArrayList<>();
        this.currentLevel = 1;
        this.levelsDir = path;
    }

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    public void startGame() {
        boolean playAgain = true;
        while (playAgain) {
            this.currentLevel = 1;
            this.enemies.clear();
            clearScreen();
            out.send("Welcome to D&D!");
            selectPlayer();
            while (currentLevel <= MAX_LEVELS && isPlayerAlive()) {
                String levelFile;
                if (levelsDir.isEmpty()) {
                    levelFile = LEVEL_FILE_PREFIX + currentLevel + LEVEL_FILE_SUFFIX;
                } else {
                    levelFile = levelsDir + File.separator + LEVEL_FILE_PREFIX + currentLevel + LEVEL_FILE_SUFFIX;
                }
                if (!loadLevel(levelFile)) {
                    out.send("Error: Could not find or load -> " + levelFile);
                    out.send("Please check if the file path is correct. Exiting game.");
                    return;
                }
                out.send("--- Loading Level " + currentLevel + " ---");
                playLevel();

                if (isPlayerAlive()) {
                    currentLevel++;
                }
            }
            out.send(isPlayerAlive() ? "You won!" : "Game Over.");
            boolean validChoice = false;
            while (!validChoice) {
                out.send("Do you want to restart (y/n)?");
                out.send("If not there will be consequences Muwhahaha.");
                String choice = scanner.nextLine().trim().toLowerCase();
                switch (choice) {
                    case "y" -> {
                        clearScreen();
                        validChoice = true;
                    }
                    case "n" -> {
                        out.send("Thank you for playing, deleting system32...");
                        validChoice = true;
                        playAgain = false;
                    }
                    default -> {
                        out.send("Invalid input! Please enter exactly 'y' or 'n'.");
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Player selection
    // -------------------------------------------------------------------------
    private void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }

    private void selectPlayer() {
        out.send("Select player:");
        out.send("1. Jon Snow     (Warrior)");
        out.send("2. The Hound    (Warrior)");
        out.send("3. Melisandre   (Mage)");
        out.send("4. Thoros of Myr (Mage)");
        out.send("5. Arya Stark   (Rogue)");
        out.send("6. Bronn        (Rogue)");
        out.send("7. Ygritte      (Hunter)");

        int choice = -1;
        boolean validChoice = false;
        while (!validChoice){
            try {
                String input = scanner.nextLine().trim();
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 7){
                    validChoice = true;
                } else {
                    out.send("Invalid choice. Please enter a number between 1 and 7.");
                }

            } catch (NumberFormatException e) {
                out.send("Invalid input. Please enter a valid number.");
            }
        }
        player = createPlayer(choice);
        player.setMessageCallback(out);
    }

    private Hero createPlayer(int choice) {
        return switch (choice) {
            case 1 -> new Warrior("Jon Snow", 300, 30, 4, 3);
            case 2 -> new Warrior("The Hound", 400, 20, 6, 5);
            case 3 -> new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
            case 4 -> new Mage("Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4);
            case 5 -> new Rogue("Arya Stark", 150, 40, 2, 20);
            case 6 -> new Rogue("Bronn", 250, 35, 3, 50);
            case 7 -> new Hunter("Ygritte", 220, 30, 2, 6);
            default -> new Warrior("Jon Snow", 300, 30, 4, 3);
        };
    }

    // -------------------------------------------------------------------------
    // Game loop
    // -------------------------------------------------------------------------

    private void playLevel() {
        while (isPlayerAlive() && !enemies.isEmpty()) {
            out.send(board.toString());
            out.send(player.description());
            char input = readInput();
            clearScreen();
            boolean validTurn = processPlayerInput(input);
            if (validTurn) {
                player.Tick();
                tickEnemies();
                enemies.removeIf(e -> {
                    if (!e.isAlive()) {
                        Position p = board.getPositionOf(e);
                        if (p != null) {
                            board.setOccupant(p, new Occupant());
                        }
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    /** Tick every living enemy: move monsters, trigger traps, double dispatch. */
    private void tickEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.processTurn(board, player);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Input handling
    // -------------------------------------------------------------------------

    private char readInput() {
        String line = scanner.nextLine();
        return line.isEmpty() ? ' ' : line.toLowerCase().charAt(0);
    }

    private boolean processPlayerInput(char input) {
        return switch (input) {
            case 'w' -> movePlayer(0, -1);
            case 's' -> movePlayer(0,  1);
            case 'a' -> movePlayer(-1, 0);
            case 'd' -> movePlayer(1,  0);
            case 'e' -> castAbility();
            case 'q' -> true;
            default  -> { 
                out.send("Invalid input. Please use w/a/s/d/e/q.");
                yield false;
            }
        };
    }

    private boolean movePlayer(int dx, int dy) {
        Position current = board.getPositionOf(player);
        Position target  = new Position(current.GetX() + dx, current.GetY() + dy);
        final boolean[] validMove = {true};
        board.getCell(target).accept(new CellVisitor() {
            @Override
            public void visit(Wall wall) {
                out.send("You cannot move there, it's a wall!");
                validMove[0] = false;
            }
            @Override
            public void visit(Floor floor) {
                board.attemptMove(player, target);
            }
        });
        return validMove[0];
    }

    private boolean castAbility() {
        Position playerPos = board.getPositionOf(player);

        List<Enemy> enemiesInRange = enemies.stream()
                .filter(e -> playerPos.Range(board.getPositionOf(e)) < player.getAbilityRange())
                .sorted(Comparator.comparingDouble(e -> playerPos.Range(board.getPositionOf(e))))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        return player.CastAbility(enemiesInRange, player);
    }

    // -------------------------------------------------------------------------
    // Level loading
    // -------------------------------------------------------------------------

    private boolean loadLevel(String filePath) {
        List<String> lines = readLevelFile(filePath);
        if (lines == null || lines.isEmpty()) return false;

        int rows = lines.size();
        int cols = lines.get(0).length();
        Cell[][] cells = new Cell[rows][cols];
        enemies.clear();

        for (int y = 0; y < rows; y++) {
            String line = lines.get(y);
            for (int x = 0; x < cols; x++) {
                cells[y][x] = parseCell(line.charAt(x), new Position(x, y));
            }
        }

        this.board = new GameBoard(cells);
        return true;
    }

    /** Returns the file's lines, or null on failure. */
    private List<String> readLevelFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return null;

        List<String> lines = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty()){
                    lines.add(line);
                }
            }
        } catch (Exception e) {
            out.send("Error reading level file: " + e.getMessage());
            return null;
        }
        return lines;
    }

    /** Convert a single map character into the correct Cell. */
    private Cell parseCell(char c, Position pos) {
        if (c == '#') return new Wall(pos);
        Floor floor = new Floor(pos);
        if (c == '@') {
            placeUnit(floor, player);
        } else if (c != '.') {
            Enemy enemy = createEnemy(c);
            if (enemy != null) {
                enemy.setMessageCallback(out);
                enemies.add(enemy);
                placeUnit(floor, enemy);
            }
        }
        return floor;
    }

    /** Helper: wrap a unit in an Occupant and set it on the floor tile. */
    private void placeUnit(Floor floor, Unit unit) {
        Occupant occ = new Occupant();
        occ.SetUnit(unit);
        floor.SetOccupant(occ);
    }

    // -------------------------------------------------------------------------
    // Enemy factory
    // -------------------------------------------------------------------------

    private Enemy createEnemy(char c) {
        return switch (c) {
            // Regular monsters
            case 's' -> new Monster('s', "Lannister Soldier", 80, 8, 3, 25, 3);
            case 'k' -> new Monster('k', "Lannister Knight", 200, 14, 8, 50, 4);
            case 'q' -> new Monster('q', "Queen's Guard", 400, 20, 15, 100, 5);
            case 'z' -> new Monster('z', "Wright", 600, 30, 15, 100, 3);
            case 'b' -> new Monster('b', "Bear-Wright", 1000, 75, 30, 250, 4);
            case 'g' -> new Monster('g', "Giant-Wright", 1500, 100, 40, 500, 5);
            case 'w' -> new Monster('w', "White Walker", 2000, 150, 50, 1000, 6);
            // Bosses
            case 'M' -> new Boss('M', "The Mountain", 1000, 60, 25, 500, 6, 5);
            case 'C' -> new Boss('C', "Queen Cersei", 100, 10, 10, 1000, 1, 8);
            case 'K' -> new Boss('K', "Night's King", 5000, 300, 150, 5000, 8, 3);
            // Traps
            case 'B' -> new Trap('B', "Bonus Trap", 1, 1, 1, 250, 1, 5);
            case 'Q' -> new Trap('Q', "Queen's Trap", 250, 50, 10, 100, 3, 7);
            case 'D' -> new Trap('D', "Death Trap", 500, 100, 20, 250, 1, 10);
            default -> null;
        };
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private boolean isPlayerAlive() {
        return player.isAlive();
    }
}