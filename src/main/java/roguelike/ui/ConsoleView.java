package roguelike.ui;

import java.util.Scanner;

import roguelike.MessageCallback;

public class ConsoleView implements GameView {
    // Terminal-based implementation of GameView interface
    private final Scanner scanner;
    private final MessageCallback out;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
        this.out = System.out::println;
    }

    public void displayMessage(String message) {
        out.send(message);
    }
    public char readChar() throws IllegalArgumentException {
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.length() != 1) {
            throw new IllegalArgumentException("Input must be a single character.");
        }
        return choice.charAt(0);
    }
    public int readInt() throws NumberFormatException {
        String input = scanner.nextLine().trim();
        return Integer.parseInt(input);
    }
    public String readString() {
        return scanner.nextLine().trim();
    }
    public void clearScreen() {
        // Clear the console screen (works in most terminals)
        out.send("\033[H\033[2J");
        System.out.flush();
    }
    public void drawBoard(String board, String playerDescription) {
        out.send(board);
        out.send(playerDescription);
    }
    
}
