package roguelike.ui;

public interface GameView {
    public void displayMessage(String message);
    public char readChar();
    public int readInt();
    public String readString();
    public void clearScreen();
    public void drawBoard(String board, String playerDescription);
}
