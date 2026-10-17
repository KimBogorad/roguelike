package roguelike;

public class Main {
    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "roguelike\\test\\resources";
        GameManager gameManager = new GameManager(path);
        gameManager.startGame();
    }
}
