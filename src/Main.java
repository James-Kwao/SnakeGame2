import javax.swing.*;
import java.awt.*;

public class Main {
    public static final JFrame frame = new JFrame("Snake Game");
    public static Image player1Head, food, map, player2Head;
    public static int tileNum = 18;
    public static int screenSize = Tiles.tileSize * tileNum;

    public Main() {
        GamePanel gamePanel = new GamePanel();
        frame.setSize(screenSize, screenSize + 60);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(gamePanel.p1th);
        frame.setUndecorated(true);
        frame.add(gamePanel, BorderLayout.SOUTH);
        frame.add(new InformPanel(gamePanel), BorderLayout.NORTH);
        frame.setResizable(false);
        frame.setVisible(true);
        gamePanel.requestFocus();
        gamePanel.requestFocusInWindow();
        frame.pack();
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(Main::new);
    }
}