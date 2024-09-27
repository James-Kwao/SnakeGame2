import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class InformPanel extends JPanel {
    private final Image food1, food2, food3, food4, player1, player2;
    private final GamePanel gamePanel;
    private Image map1, map2;
    private Point initPoint;

    public InformPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setPreferredSize(new Dimension(Main.screenSize, 60));
        setBackground(Color.black);
        food1 = gamePanel.foodImg1.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        food2 = gamePanel.foodImg2.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        food3 = gamePanel.foodImg3.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        food4 = gamePanel.foodImg4.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        player1 = gamePanel.p1th.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        player2 = gamePanel.p2th.getScaledInstance(20, 20, Image.SCALE_SMOOTH);

        try {
            map1 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/map/sGrass.png")));
            map2 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/map/gGrass.png")));
        } catch (IOException ignored) {
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                initPoint = e.getPoint();
                gamePanel.keyHandler.isPause = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                initPoint = null;
                gamePanel.window.requestFocus();
                transferFocus();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Main.frame.setBounds(e.getXOnScreen() - initPoint.x, e.getYOnScreen() - initPoint.y,
                        Main.screenSize, Main.screenSize + 60);
                Rectangle frameLocation = Main.frame.getBounds();
                if (gamePanel.window != null) {
                    int size = gamePanel.window.getWidth();
                    gamePanel.window.setBounds((int) (frameLocation.getCenterX() - (size / 2)),
                            (int) (frameLocation.getCenterY() - (size / 2) + 30), size, size);
                }
            }
        });

        animate();
    }

    private void animate() {
        Timer timer = new Timer(10, e -> {
            if (gamePanel.life < 0) gamePanel.life = 0;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int mouseX = -10, mouseY = -10;
        try {
            mouseX = getMousePosition() != null ? getMousePosition().x : -10;
            mouseY = getMousePosition() != null ? getMousePosition().y : -10;
        } catch (Exception ignored) {
        }

        setCursor(getBounds().contains(mouseX, mouseY) ? Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) :
                Cursor.getDefaultCursor());

        int x = -30;
        for (int i = 0; i < 11; i++) {
            g2.drawImage(gamePanel.mapCounter == 1 ? map1 : map2, x, 0, null);
            x += 60;
        }

        x = 300;
        int xPos;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Papyrus", Font.BOLD, 20));

        xPos = Main.screenSize - x;
        g2.drawImage(food4, xPos, 20, null);
        g2.drawString(String.valueOf(gamePanel.f_count4), xPos + 25, 35);

        xPos = Main.screenSize - (x -= 75);
        g2.drawImage(food2, xPos, 20, null);
        g2.drawString(String.valueOf(gamePanel.f_count2), xPos + 25, 35);

        xPos = Main.screenSize - (x -= 75);
        g2.drawImage(food3, xPos, 20, null);
        g2.drawString(String.valueOf(gamePanel.f_count3), xPos + 25, 35);

        xPos = Main.screenSize - (x - 75);
        g2.drawImage(food1, xPos, 20, null);
        g2.drawString(String.valueOf(gamePanel.f_count1), xPos + 25, 35);

        //TODO: check game score is it's displaying proper number
        if (gamePanel.numPlayers != 2) {
            x = gamePanel.gameLevel >= 11 ? 12 : gamePanel.gameLevel;
            g2.drawString(STR."Score: \{x * gamePanel.player1Body.size()}", 10, 35);


            xPos = 10 + g2.getFontMetrics().stringWidth(STR." Score: \{x}");
            if (gamePanel.keyHandler.keyTyped.length() >= 3)
                g2.drawString(gamePanel.keyHandler.keyTyped, xPos += 40, 35);

            xPos += 70;
            g2.setColor(Color.black);
            g2.drawImage(gamePanel.life > 0 ? gamePanel.full_life : gamePanel.emp_life, xPos, 20, null);
            g2.drawString(STR."x \{gamePanel.life}", xPos + 30, 35);
        } else {
            g2.setColor(Color.black);
            g2.drawImage(player1, 20, 20, null);
            g2.drawString(STR."x \{gamePanel.player1Body.size()}", 45, 35);
            g2.drawImage(player2, 150, 20, null);
            g2.drawString(STR."x \{gamePanel.player2Body.size()}", 175, 35);
        }

        g.dispose();
        System.gc();
    }
}
