package roguelike;

import roguelike.ui.*;

public class Main {
    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "src\\test\\resources";
        GameView view = new ConsoleView();
        GameManager gameManager = new GameManager(path, view);
        gameManager.startGame();
    }
}
