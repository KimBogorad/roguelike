package roguelike;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import roguelike.model.units.*;
import roguelike.world.*;
import roguelike.ui.GameView;

public class GameManager {

    private static final int MAX_LEVELS = 4;
    private static final String LEVEL_FILE_PREFIX = "level";
    private static final String LEVEL_FILE_SUFFIX = ".txt";

    private GameBoard board;
    private Hero player;
    private List<Enemy> enemies;
    
    
    private int currentLevel;
    private String levelsDir;
    private final EnemyFactory enemyFactory;
    private final HeroFactory heroFactory;
    private final GameView view;

    /** Inject a custom output sink. */
    public GameManager(String path, GameView view) {
        this.enemies = new ArrayList<>();
        this.currentLevel = 1;
        this.levelsDir = path;
        this.enemyFactory = new EnemyFactory();
        this.heroFactory = new HeroFactory();
        this.view = view;
    }

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    public void startGame() {
        boolean playAgain = true;
        while (playAgain) {
            this.currentLevel = 1;
            this.enemies.clear();
            view.clearScreen();
            view.displayMessage("Welcome to D&D!");
            selectPlayer();
            while (currentLevel <= MAX_LEVELS && isPlayerAlive()) {
                String levelFile;
                if (levelsDir.isEmpty()) {
                    levelFile = LEVEL_FILE_PREFIX + currentLevel + LEVEL_FILE_SUFFIX;
                } else {
                    levelFile = levelsDir + File.separator + LEVEL_FILE_PREFIX + currentLevel + LEVEL_FILE_SUFFIX;
                }
                if (!loadLevel(levelFile)) {
                    view.displayMessage("Error: Could not find or load -> " + levelFile);
                    view.displayMessage("Please check if the file path is correct. Exiting game.");
                    return;
                }
                view.displayMessage("--- Loading Level " + currentLevel + " ---");
                playLevel();

                if (isPlayerAlive()) {
                    currentLevel++;
                }
            }
            view.displayMessage(isPlayerAlive() ? "You won!" : "Game Over.");
            boolean validChoice = false;
            while (!validChoice) {
                view.displayMessage("Do you want to restart (y/n)?");
                view.displayMessage("If not there will be consequences Muwhahaha.");
                char choice = ' ';
                try {
                    choice = view.readChar();
                } catch (IllegalArgumentException e) {
                    view.displayMessage("Invalid input. Please enter a single character.");
                    continue;
                }
                switch (choice) {
                    case 'y' -> {
                        view.clearScreen();
                        validChoice = true;
                    }
                    case 'n' -> {
                        view.displayMessage("Thank you for playing, deleting system32...");
                        validChoice = true;
                        playAgain = false;
                    }
                    default -> {
                        view.displayMessage("Invalid input! Please enter exactly 'y' or 'n'.");
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Player selection
    // -------------------------------------------------------------------------

    private void selectPlayer() {
        view.displayMessage("Select player:");
        view.displayMessage("1. Jon Snow     (Warrior)");
        view.displayMessage("2. The Hound    (Warrior)");
        view.displayMessage("3. Melisandre   (Mage)");
        view.displayMessage("4. Thoros of Myr (Mage)");
        view.displayMessage("5. Arya Stark   (Rogue)");
        view.displayMessage("6. Bronn        (Rogue)");
        view.displayMessage("7. Ygritte      (Hunter)");

        int choice = -1;
        boolean validChoice = false;
        while (!validChoice){
            try {
                choice = view.readInt();
                if (choice >= 1 && choice <= 7){
                    validChoice = true;
                } else {
                    view.displayMessage("Invalid choice. Please enter a number between 1 and 7.");
                }

            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input. Please enter a valid number.");
            }
        }
        player = heroFactory.createPlayer(choice);
        player.setMessageCallback(m -> view.displayMessage(m));
    }

    

    // -------------------------------------------------------------------------
    // Game loop
    // -------------------------------------------------------------------------

    private void playLevel() {
        while (isPlayerAlive() && !enemies.isEmpty()) {
            view.drawBoard(board.toString(), player.description());
            char input = ' ';
            try {
                input = view.readChar();
            } catch (IllegalArgumentException e) {
                view.displayMessage("Invalid input. Please enter a single character.");
                continue;
            } 
            view.clearScreen();
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

    private boolean processPlayerInput(char input) {
        return switch (input) {
            case 'w' -> movePlayer(0, -1);
            case 's' -> movePlayer(0,  1);
            case 'a' -> movePlayer(-1, 0);
            case 'd' -> movePlayer(1,  0);
            case 'e' -> castAbility();
            case 'q' -> true;
            default  -> { 
                view.displayMessage("Invalid input. Please use w/a/s/d/e/q.");
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
                view.displayMessage("You cannot move there, it's a wall!");
                validMove[0] = false;
            }
            @Override
            public void visit(Floor floor) {
                board.attemptMove(player, target);
            }
        });
        return validMove[0];
    }

    // Helper Function: use stream() to create an arraylist of all enemies in hero's ability_range.
    // Sorted by proximity (closer range)
    // return true if ability was cast, false otherwise
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
            view.displayMessage("Error reading level file: " + e.getMessage());
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
            Enemy enemy = enemyFactory.createEnemy(c);
            if (enemy != null) {
                enemy.setMessageCallback(m -> view.displayMessage(m));
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
    // Utility
    // -------------------------------------------------------------------------

    private boolean isPlayerAlive() {
        return player.isAlive();
    }
}