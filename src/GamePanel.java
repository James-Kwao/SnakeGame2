import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class GamePanel extends JPanel {
    public static final String[] instructions = {"NAVIGATION:",
            "````````````````````````````````````````````````````````````````````````",
            "** Use the \"UP\" \"RIGHT\" \"DOWN\" and \"LEFT\" arrow ",
            "    keys to move up, right, down and left respectively",
            "    in single player mode or 1 player.",
            "** Player 2 can use \"W\" to move up \"A\" to move",
            "    left \"S\" to move down and \"D\" to move left",
            "    in  multi-player mode.",
            "````````````````````````````````````````````````````````````````````````",
            "                                                                        ",
            "RULES:\n",
            "````````````````````````````````````````````````````````````````````````",
            "** Collect more eggs to gain more points.",
            "** Do not collide snake head with its own body.",
            "** In multi-player mode, player 1 can collide it's snake",
            "    head into player 2's body, same effect applies",
            "    to player 2.",
            "````````````````````````````````````````````````````````````````````````"};
    final KeyHandler keyHandler;
    final SoundManger soundManger;
    Robot b;
    JWindow window;
    Image p1dh, p1lh, p1th, p1tb1, p1tb2, p1rh, p1rb1, p1rb2,
            p2dh, p2lh, p2th, p2tb1, p2tb2, p2rh, p2rb1, p2rb2,
            sGrass, gGrass, foodImg1, foodImg2, foodImg3, foodImg4, full_life, emp_life, close;
    Random random = new Random();
    Timer timer;
    int start, player1xSpeed, player1ySpeed, player2xSpeed, player2ySpeed,
            player2BodyCount, player1BodyCount, mapCounter = 1, life = 3,
            gameLevel = 10, preGameLevel, preNumPlayers, numPlayers = 1, c;
    Tiles player1Head, food, player2Head;
    ArrayList<Tiles> player1Body, player2Body;
    int f_count1, f_count2, f_count3, f_count4, helpScroll;
    boolean music = true, sound = true;
    ScheduledExecutorService service;
    private char selector = 'r';
    private RoundRectangle2D.Float resh;

    public GamePanel() {
        setPreferredSize(new Dimension(Main.screenSize, Main.screenSize));
        setBackground(Color.black);
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0), null));
        try {
            p1th = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1th.png")));
            p1tb1 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1tb1.png")));
            p1tb2 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1tb2.png")));

            p1rh = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1rh.png")));
            p1rb1 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1rb1.png")));
            p1rb2 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1rb2.png")));

            p1dh = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1dh.png")));
            p1lh = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player1/p1lh.png")));

            p2th = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2th.png")));
            p2tb1 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2tb1.png")));
            p2tb2 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2tb2.png")));

            p2rh = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2rh.png")));
            p2rb1 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2rlb1.png")));
            p2rb2 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2rlb2.png")));

            p2dh = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2dh.png")));
            p2lh = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/player2/p2lh.png")));

            gGrass = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/map/gGrass.png")));
            gGrass = gGrass.getScaledInstance(35, 35, Image.SCALE_SMOOTH);

            sGrass = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/map/sGrass.png")));
            sGrass = sGrass.getScaledInstance(35, 35, Image.SCALE_SMOOTH);

            foodImg1 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/food/food_1.png")));
            foodImg2 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/food/food_2.png")));
            foodImg3 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/food/food_3.png")));
            foodImg4 = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/food/food_4.png")));

            full_life = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/others/full_life.png")));
            emp_life = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/others/emp_life.png")));

            close = ImageIO.read(Objects.requireNonNull(getSystemResourceAsStream("res/others/close.png")));
            close = close.getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            b = new Robot();
        } catch (Exception ignored) {
        }
        keyHandler = new KeyHandler();
        soundManger = new SoundManger();
        preGameLevel = gameLevel;
        preNumPlayers = numPlayers;
        player1Body = new ArrayList<>();
        player2Body = new ArrayList<>();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                requestFocus();
                setFocusable(true);
                if (keyHandler.isPause) window.requestFocus();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                keyHandler.player1KeyPad(e);
                if (numPlayers == 2)
                    keyHandler.player2KeyPad(e);
            }
        });
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (keyHandler.isPause)window.requestFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {
                keyHandler.isPause = true;

            }
        });
        characterPreset();
        gameLoop();
    }

    private void gameLoop() {
        try {
            timer = new Timer(1100 - (gameLevel * 100), _ -> {
                movePlayer1Head();
                checkFoodEaten(false);
                moveBody(player1Body, player1xSpeed, player1ySpeed, player1Head);

                player1Head.xPos += (player1xSpeed * Tiles.tileSize);
                player1Head.yPos += (player1ySpeed * Tiles.tileSize);

                if (numPlayers == 2) {
                    movePlayer2Head();
                    checkFoodEaten(true);
                    moveBody(player2Body, player2xSpeed, player2ySpeed, player2Head);
                    player2Head.xPos += (player2xSpeed * Tiles.tileSize);
                    player2Head.yPos += (player2ySpeed * Tiles.tileSize);
                } else {
                    implementCheats();
                }
                repaint();
            });
            timer.start();
        } catch (Exception ignored) {

        }

    }

    private void checkFoodEaten(boolean is2Player) {
        if (hasCollided(is2Player ? player2Head : player1Head, food)) {
            if (sound)
                Toolkit.getDefaultToolkit().beep();
            switch (food.start) {
                case 1:
                    f_count1++;
                    break;
                case 2:
                    f_count2++;
                    break;
                case 3:
                    f_count3++;
                    break;
                case 4:
                    f_count4++;
                    break;
            }
            placeFood();
            if (is2Player) {
                player2BodyCount = player2BodyCount == 1 ? 2 : 1;
                player2Body.add(new Tiles(food.xPos, food.yPos, start, player2BodyCount));
            } else {
                player1BodyCount = player1BodyCount == 1 ? 2 : 1;
                player1Body.add(new Tiles(food.xPos, food.yPos, start, player1BodyCount));
            }
        }
    }

    private void drawGameMap(Graphics2D g2) {
        for (int i = 0; i < Main.tileNum; i++) {
            g2.drawImage(Main.map, 0, i * Tiles.tileSize, null);
            for (int j = 1; j < Main.tileNum; j++) {
                g2.drawImage(Main.map, j * Tiles.tileSize, i * Tiles.tileSize, null);
            }
        }
    }

    private void movePlayer1Head() {
        if ((player1Head.xPos / Tiles.tileSize) >= Main.tileNum) player1Head.xPos -= player1Head.xPos;
        if ((player1Head.yPos / Tiles.tileSize) >= Main.tileNum) player1Head.yPos -= player1Head.yPos;
        if (player1Head.xPos < 0) player1Head.xPos = getWidth() - Tiles.tileSize;
        if (player1Head.yPos < 0) player1Head.yPos = getHeight() - Tiles.tileSize;

        if (keyHandler.isPlayer1Up && player1ySpeed != 1 && player1Head.start != 2) {
            Main.player1Head = p1th;
            player1xSpeed = 0;
            player1ySpeed = -1;
            player1Head.start = start = 1;
        }
        if (keyHandler.isPlayer1Down && player1ySpeed != -1 && player1Head.start != 1) {
            Main.player1Head = p1dh;
            player1xSpeed = 0;
            player1ySpeed = 1;
            player1Head.start = start = 2;
        }
        if (keyHandler.isPlayer1Left && player1xSpeed != 1 && player1Head.start != 4) {
            Main.player1Head = p1lh;
            player1xSpeed = -1;
            player1ySpeed = 0;
            player1Head.start = start = 3;
        }
        if (keyHandler.isPlayer1Right && player1xSpeed != -1 && player1Head.start != 3) {
            Main.player1Head = p1rh;
            player1xSpeed = 1;
            player1ySpeed = 0;
            player1Head.start = start = 4;
        }
        if (keyHandler.isReset) {
            player1Body = new ArrayList<>();
            if (numPlayers == 2)
                player2Body = new ArrayList<>();
            characterPreset();
            keyHandler.isReset = false;
            life = 3;
            System.gc();
        }
        if (keyHandler.isPause) {
            pauseDisplay();
            player1xSpeed = player1ySpeed = 0;
            keyHandler.isPlayer1Right = keyHandler.isPlayer1Left = keyHandler.isPlayer1Down = keyHandler.isPlayer1Up = false;
        }
    }

    private void movePlayer2Head() {
        if ((player2Head.xPos / Tiles.tileSize) >= Main.tileNum) player2Head.xPos -= player2Head.xPos;
        if ((player2Head.yPos / Tiles.tileSize) >= Main.tileNum) player2Head.yPos -= player2Head.yPos;
        if (player2Head.xPos < 0) player2Head.xPos = getWidth() - Tiles.tileSize;
        if (player2Head.yPos < 0) player2Head.yPos = getHeight() - Tiles.tileSize;

        if (keyHandler.isPlayer2Up && player2ySpeed != 1 && player2Head.start != 2) {
            Main.player2Head = p2th;
            player2xSpeed = 0;
            player2ySpeed = -1;
            player2Head.start = start = 1;
        }
        if (keyHandler.isPlayer2Down && player2ySpeed != -1 && player2Head.start != 1) {
            Main.player2Head = p2dh;
            player2xSpeed = 0;
            player2ySpeed = 1;
            player2Head.start = start = 2;
        }
        if (keyHandler.isPlayer2Left && player2xSpeed != 1 && player2Head.start != 4) {
            Main.player2Head = p2lh;
            player2xSpeed = -1;
            player2ySpeed = 0;
            player2Head.start = start = 3;
        }
        if (keyHandler.isPlayer2Right && player2xSpeed != -1 && player2Head.start != 3) {
            Main.player2Head = p2rh;
            player2xSpeed = 1;
            player2ySpeed = 0;
            player2Head.start = start = 4;
        }
        if (keyHandler.isPause) {
            player2xSpeed = player2ySpeed = 0;
            keyHandler.isPlayer2Right = keyHandler.isPlayer2Left = keyHandler.isPlayer2Down = keyHandler.isPlayer2Up = false;
        }
    }

    private void moveBody(ArrayList<Tiles> body, int xSpeed, int ySpeed, Tiles head) {
        if (!keyHandler.isPause && (xSpeed != 0 || ySpeed != 0))
            for (int i = body.size() - 1; i >= 0; i--) {
                Tiles snakePart = body.get(i);
                if (i == 0) {
                    snakePart.xPos = head.xPos;
                    snakePart.yPos = head.yPos;
                    snakePart.start = start;
                } else {
                    Tiles prevPart = body.get(i - 1);
                    snakePart.xPos = prevPart.xPos;
                    snakePart.yPos = prevPart.yPos;
                    snakePart.start = prevPart.start;
                }
                if (hasCollided(food, snakePart)) {
                    System.out.println(STR."has collided at point \{food.xPos}\t\{food.yPos}");
                    food = null;
                    placeFood();
                }
            }
    }

    private boolean hasCollided(Tiles tile1, Tiles tile2) {
        return tile1.xPos == tile2.xPos && tile1.yPos == tile2.yPos;
    }

    private void placeFood() {
        food = new Tiles(random.nextInt(Main.screenSize / Tiles.tileSize),
                random.nextInt(Main.screenSize / Tiles.tileSize),
                random.nextInt(1, 5), 0);
        switch (food.start) {
            case 1:
                Main.food = foodImg1;
                break;
            case 2:
                Main.food = foodImg2;
                break;
            case 3:
                Main.food = foodImg3;
                break;
            case 4:
                Main.food = foodImg4;
                break;
        }
    }

    private void characterPreset() {
        start = random.nextInt(1, 5);
        Main.map = gGrass;
        mapCounter = 1;
        player1Head = new Tiles(random.nextInt(Main.screenSize / Tiles.tileSize),
                random.nextInt(Main.screenSize / Tiles.tileSize), start, 0);

        food = new Tiles(random.nextInt(Main.screenSize / Tiles.tileSize),
                random.nextInt(Main.screenSize / Tiles.tileSize), start, 0);

        if (numPlayers == 2) {
            player2Head = new Tiles(random.nextInt(Main.screenSize / Tiles.tileSize),
                    random.nextInt(Main.screenSize / Tiles.tileSize), start, 0);
            player2Body = new ArrayList<>();
            player2xSpeed = player2ySpeed = 0;
            player2BodyCount = 1;
        }
        switch (start) {
            case 1:
                Main.player1Head = p1th;
                Main.food = foodImg1;
                if (numPlayers == 2) Main.player2Head = p2th;
                break;
            case 2:
                Main.player1Head = p1dh;
                Main.food = foodImg2;
                if (numPlayers == 2) Main.player2Head = p2dh;
                break;
            case 3:
                Main.player1Head = p1lh;
                Main.food = foodImg3;
                if (numPlayers == 2) Main.player2Head = p2lh;
                break;
            case 4:
                Main.player1Head = p1rh;
                Main.food = foodImg4;
                if (numPlayers == 2) Main.player2Head = p2rh;
                break;
        }
        player1xSpeed = player1ySpeed = 0;
        f_count1 = f_count2 = f_count3 = f_count4 = 0;
        player1Body = new ArrayList<>();
        player1BodyCount = 1;
        System.gc();
    }

    private void implementCheats() {
        switch (keyHandler.cheatType) {
            case 's':
                mapCounter = mapCounter == 1 ? 2 : 1;
                Main.map = mapCounter == 1 ? gGrass : sGrass;
                keyHandler.cheatType = ' ';
                break;
            case 'l':
                life++;
                keyHandler.cheatType = ' ';
                break;
            case 'd':
                life--;
                keyHandler.cheatType = ' ';
                break;
            case 'r':
                keyHandler.isPause = true;
                keyHandler.cheatType = ' ';
                break;
            case 'm':
                b.mouseMove((int) Main.frame.getBounds().getCenterX(), Main.frame.getY() + 30);
                keyHandler.cheatType = ' ';
                break;
            case 'S':
                System.out.println("Save game instance");
                keyHandler.cheatType = ' ';
                break;
            case 'R':
                keyHandler.isReset = true;
                keyHandler.cheatType = ' ';
                break;
        }
    }

    public void pauseDisplay() {
        if (music) soundManger.playMusic();
        int size = Main.screenSize / 2;
        Rectangle frameLocation = Main.frame.getBounds();
        System.gc();
        window = new JWindow(Main.frame) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.white);

                g2.setFont(new Font("Kristen ITC", Font.ITALIC, 40));
                FontMetrics fm = g2.getFontMetrics();

                String s = "PAUSED";
                int xPos = size / 2 - (fm.stringWidth(s) / 2);
                int yPos = fm.getHeight() - fm.getAscent() / 3;
                g2.drawString(s, xPos, yPos);

                g2.setColor(Color.black);
                g2.fillRect(0, yPos + 14, size, 2);
                g2.fillRect(0, yPos += 20, size, 2);


                g2.setFont(new Font("Papyrus", Font.ITALIC | Font.BOLD, 30));
                fm = g2.getFontMetrics();

                {
                    if ((preGameLevel != gameLevel) ||
                        (preNumPlayers != numPlayers)) s = "Restart";
                    else s = "Resume";

                    xPos = size / 2 - (fm.stringWidth(s) / 2);
                    yPos += fm.getHeight();
                    g2.setColor(new Color(0xB501FBA8, true));
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == 'r')
                        g2.fill(resh);
                    g2.setColor(new Color(0x060A43));
                    g2.drawString(s, xPos, yPos);
                }

                {
                    s = "Exit";
                    xPos = size / 2 - (fm.stringWidth(s) / 2);
                    yPos += fm.getHeight();
                    g2.setColor(new Color(0xB501FBA8, true));
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == 'e')
                        g2.fill(resh);
                    g2.setColor(new Color(0x060A43));
                    g2.drawString(s, xPos, yPos);
                }

                {
                    s = "Settings";
                    xPos = size / 2 - (fm.stringWidth(s) / 2);
                    yPos += fm.getHeight() + 5;
                    g2.setColor(new Color(0xB501FBA8, true));
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == 's')
                        g2.fill(resh);
                    g2.setColor(new Color(0x060A43));
                    g2.drawString(s, xPos, yPos);
                }

                {
                    s = "Help";
                    yPos += (fm.getHeight() + 5);
                    xPos = size / 2 - (fm.stringWidth(s) / 2);
                    g2.setColor(new Color(0xB501FBA8, true));
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == 'h')
                        g2.fill(resh);
                    g2.setColor(new Color(0x060A43));
                    g2.drawString(s, xPos, yPos);
                }

                repaint();
                g.dispose();
                System.gc();
            }
        };
        window.setBackground(new Color(0x801E5D46, true));
        window.setPreferredSize(new Dimension(size, size));
        window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                , new Point(0, 0), null));
        window.setLocation((int) (frameLocation.getCenterX() - (size / 2)), (int) (frameLocation.getCenterY() - (size / 2) + 30));
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE, KeyEvent.VK_P:
                        soundManger.stopMusic();
                        timer.restart();
                        if ((preGameLevel != gameLevel) || (preNumPlayers != numPlayers)) {
                            characterPreset();
                            GamePanel.this.repaint();
                            preGameLevel = gameLevel;
                            preNumPlayers = numPlayers;
                            timer.setDelay(gameLevel == 30 ? 30 : 1100 - (gameLevel * 100));
                            timer.restart();
                        }
                        keyHandler.isPause = false;
                        selector = 'r';
                        window.dispose();
                        System.gc();
                        break;
                    case KeyEvent.VK_DOWN:
                        switch (selector) {
                            case 'r':
                                selector = 'e';
                                break;
                            case 'e':
                                selector = 's';
                                break;
                            case 's':
                                selector = 'h';
                                break;
                            case 'h':
                                selector = 'r';
                                break;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        switch (selector) {
                            case 'r':
                                selector = 'h';
                                break;
                            case 'e':
                                selector = 'r';
                                break;
                            case 's':
                                selector = 'e';
                                break;
                            case 'h':
                                selector = 's';
                                break;
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        switch (selector) {
                            case 'r':
                                timer.setDelay(gameLevel == 30 ? 30 : 1100 - (gameLevel * 100));
                                timer.restart();
                                if ((preGameLevel != gameLevel) || (preNumPlayers != numPlayers))
                                    characterPreset();
                                GamePanel.this.repaint();
                                preGameLevel = gameLevel;
                                preNumPlayers = numPlayers;
                                soundManger.stopMusic();
                                window.dispose();
                                keyHandler.isPause = false;
                                System.gc();
                                break;
                            case 'e':
                                soundManger.exit();
                                window.dispose();
                                Main.frame.dispose();
                                System.gc();
                                System.exit(0);
                                break;
                            case 's':
                                settingsDisplay();

                                System.gc();
                                break;
                            case 'h':
                                helpDisplay();
                                break;
                        }
                        break;
                }
            }
        });
        window.setFocusable(true);
        window.pack();
        window.setVisible(true);
        window.validate();
        timer.stop();
    }

    public void settingsDisplay() {
        window.dispose();
        System.gc();
        int size = Main.screenSize / 2;
        selector = '1';
        Rectangle frameLocation = Main.frame.getBounds();
        window = new JWindow(Main.frame) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0xB501FBA8));

                g2.setFont(new Font("Kristen ITC", Font.ITALIC, 40));
                FontMetrics fm = g2.getFontMetrics();

                String s = "Settings";
                int xPos = size / 2 - (fm.stringWidth(s) / 2);
                int yPos = fm.getHeight() - fm.getAscent() / 3;
                g2.drawString(s, xPos, yPos);

                g2.setColor(new Color(0xB501FBA8, true));
                g2.fillRect(0, yPos + 14, size, 2);
                g2.fillRect(0, yPos += 20, size, 2);


                g2.setFont(new Font("Papyrus", Font.BOLD, 25));
                fm = g2.getFontMetrics();

                {
                    s = "Back";
                    xPos = size / 2 - (fm.stringWidth(s) / 2);
                    yPos += fm.getHeight();
                    g2.setColor(Color.black);
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == '1')
                        g2.fill(resh);
                    g2.setColor(Color.WHITE);
                    g2.drawString(s, xPos, yPos);
                }

                {
                    s = "Game Sound";
                    xPos = 10;
                    yPos += fm.getHeight();
                    g2.setColor(Color.black);
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == '2')
                        g2.fill(resh);
                    g2.setColor(Color.WHITE);
                    g2.drawString(s, xPos, yPos);
                    s = sound ? "on" : "off";
                    g2.drawString(s, size - 10 - fm.stringWidth(s), yPos);
                }

                {
                    s = "Music";
                    yPos += fm.getHeight() + 5;
                    g2.setColor(Color.black);
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == '3')
                        g2.fill(resh);
                    g2.setColor(Color.WHITE);
                    g2.drawString(s, xPos, yPos);
                    s = music ? "on" : "off";
                    g2.drawString(s, size - 10 - fm.stringWidth(s), yPos);
                }

                {
                    s = "Game Speed";
                    yPos += (fm.getHeight() + 5);
                    g2.setColor(Color.black);
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == '4')
                        g2.fill(resh);
                    g2.setColor(Color.WHITE);
                    g2.drawString(s, xPos, yPos);
                    s = gameLevel == 30 ? "gods" : STR."\{gameLevel}0";
                    g2.drawString(s, size - 10 - fm.stringWidth(s), yPos);
                }

                {
                    s = "Player Mode";
                    yPos += (fm.getHeight() + 5);
                    g2.setColor(Color.black);
                    resh = new RoundRectangle2D.Float(5, yPos - fm.getAscent() + 3, size - 10,
                            fm.getHeight() - fm.getAscent() / 2f + 6, 20, 20);
                    if (selector == '5')
                        g2.fill(resh);
                    g2.setColor(Color.WHITE);
                    g2.drawString(s, xPos, yPos);
                    s = String.valueOf(numPlayers);
                    g2.drawString(s, size - 10 - fm.stringWidth(s), yPos);
                }

                repaint();
                g.dispose();
                System.gc();
            }
        };
        window.setBackground(new Color(0x80353803, true));
        window.setPreferredSize(new Dimension(size, size));
        window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                , new Point(0, 0), null));
        window.setLocation((int) (frameLocation.getCenterX() - (size / 2)), (int) (frameLocation.getCenterY() - (size / 2) + 30));
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        switch (selector) {
                            case '1':
                                selector = '2';
                                break;
                            case '2':
                                selector = '3';
                                break;
                            case '3':
                                selector = '4';
                                break;
                            case '4':
                                selector = '5';
                                break;
                            case '5':
                                selector = '1';
                                break;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        switch (selector) {
                            case '1':
                                selector = '5';
                                break;
                            case '2':
                                selector = '1';
                                break;
                            case '3':
                                selector = '2';
                                break;
                            case '4':
                                selector = '3';
                                break;
                            case '5':
                                selector = '4';
                                break;
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        switch (selector) {
                            case '1':
                                selector = 's';
                                window.dispose();
                                System.gc();
                                pauseDisplay();
                                break;
                            case '2':
                                sound = !sound;
                                break;
                            case '3':
                                music = !music;
                                break;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        switch (selector) {
                            case '2':
                                sound = !sound;
                                break;
                            case '3':
                                music = !music;
                                break;
                            case '4':
                                gameLevel++;
                                if (gameLevel > 11) gameLevel = 1;
                                if (gameLevel == 11) gameLevel = 30;
                                break;
                            case '5':
                                numPlayers++;
                                if (numPlayers > 2) numPlayers = 1;
                                break;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        switch (selector) {
                            case '2':
                                sound = !sound;
                                break;
                            case '3':
                                music = !music;
                                break;
                            case '4':
                                gameLevel--;
                                if (gameLevel < 1) gameLevel = 11;
                                if (gameLevel > 11) gameLevel = 10;
                                if (gameLevel == 11) gameLevel = 30;
                                break;
                            case '5':
                                numPlayers--;
                                if (numPlayers < 1) numPlayers = 2;
                        }
                        break;
                }
            }
        });
        window.setFocusable(true);
        window.pack();
        window.setVisible(true);
        window.validate();
    }

    public void helpDisplay() {
        window.dispose();
        ArrayList<String> strings = new ArrayList<>();
        System.gc();
        helpScroll = 0;
        int size = Main.screenSize / 2 + 100;
        Rectangle frameLocation = Main.frame.getBounds();
        Rectangle2D.Float closeBtn = new Rectangle2D.Float(size - 50, 20, 20, 20);
        window = new JWindow(Main.frame) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                RoundRectangle2D.Float con = new RoundRectangle2D.Float(0, 0, size, size, 80, 80);
                g2.setColor(Color.BLACK);
                g2.fill(con);
                con = new RoundRectangle2D.Float(0, 0, size, size, 80, 80);
                g2.setStroke(new BasicStroke(4));
                g2.setColor(Color.WHITE);
                g2.draw(con);

                g2.setFont(new Font("Yu Gothic", Font.PLAIN, 15));
                FontMetrics fm = g2.getFontMetrics();

                int yPos = 30 + helpScroll;
                for (String s : strings) {
                    g2.drawString(s, 20, yPos += fm.getHeight());
                }

                g2.drawImage(close, size - 50, 20, null);

                Point mousePoint = null;
                try {
                    mousePoint = getMousePosition() != null ? getMousePosition() : new Point(-100, -100);
                } catch (Exception ignored) {
                }

                setCursor(closeBtn.contains(mousePoint) ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

                g.dispose();
            }
        };
        window.setBackground(new Color(0x0353803, true));
        window.setPreferredSize(new Dimension(size, size));
        window.setCursor(Cursor.getDefaultCursor());
        window.setLocation((int) (frameLocation.getCenterX() - (size / 2)), (int) (frameLocation.getCenterY() - (size / 2) + 30));
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (closeBtn.contains(e.getPoint())) {
                    selector = 'h';
                    service.close();
                    window.dispose();
                    c = 0;
                    pauseDisplay();
                }
            }
        });
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (helpScroll != -80)
                            helpScroll -= 10;
                        break;
                    case KeyEvent.VK_UP:
                        if (helpScroll != 0)
                            helpScroll += 10;
                        break;
                    default:
                        selector = 'h';
                        service.close();
                        window.dispose();
                        c = 0;
                        pauseDisplay();
                        break;
                }

            }
        });
        window.setFocusable(true);
        window.pack();
        window.setVisible(true);
        window.validate();

        b.mouseMove((int) window.getBounds().getMaxX() - 40, (int) window.getBounds().getMinY() + 30);

        try {
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> {
                if (c != instructions.length) {
                    strings.add(instructions[c]);
                    c++;
                }
                window.repaint();
            }, 0, 300, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGameMap(g2);
        g2.drawImage(Main.food, food.xPos, food.yPos, null);


        if (numPlayers == 2) {
            if (player2Body.size() >= player1Body.size()) {
                drawPlayer1Parts(g2);
                g2.drawImage(Main.player1Head, player1Head.xPos, player1Head.yPos, null);
                drawPlayer2Parts(g2);
                g2.drawImage(Main.player2Head, player2Head.xPos, player2Head.yPos, null);
            } else {
                drawPlayer2Parts(g2);
                g2.drawImage(Main.player2Head, player2Head.xPos, player2Head.yPos, null);
                drawPlayer1Parts(g2);
                g2.drawImage(Main.player1Head, player1Head.xPos, player1Head.yPos, null);
            }
        } else {
            drawPlayer1Parts(g2);
            g2.drawImage(Main.player1Head, player1Head.xPos, player1Head.yPos, null);
        }

        g.dispose();
        System.gc();
    }

    private void drawPlayer1Parts(Graphics2D g2) {
        for (Tiles tiles : player1Body)
            switch (tiles.start) {
                case 1, 2:
                    g2.drawImage(tiles.bodyShape == 1 ? p1tb1 : p1tb2, tiles.xPos, tiles.yPos, null);
                    break;
                case 3, 4:
                    g2.drawImage(tiles.bodyShape == 1 ? p1rb1 : p1rb2, tiles.xPos, tiles.yPos, null);
                    break;
            }
    }

    private void drawPlayer2Parts(Graphics2D g2) {
        for (Tiles tiles : player2Body)
            switch (tiles.start) {
                case 1, 2:
                    g2.drawImage(tiles.bodyShape == 1 ? p2tb1 : p2tb2, tiles.xPos, tiles.yPos, null);
                    break;
                case 3, 4:
                    g2.drawImage(tiles.bodyShape == 1 ? p2rb1 : p2rb2, tiles.xPos, tiles.yPos, null);
                    break;
            }
    }
}
