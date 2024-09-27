import java.awt.event.KeyEvent;

public class KeyHandler {
    private final String[] cheats = {"swax", "life", "dead", "resh", "move", "save", "rest"};
    public boolean isPlayer1Up, isPlayer1Down, isPlayer1Left, isPlayer1Right, isPause, isReset;
    public boolean isPlayer2Up, isPlayer2Down, isPlayer2Left, isPlayer2Right;
    public char cheatType;
    String keyTyped = "";
    private int c = 0;

    public void player1KeyPad(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (!isPause)
                    isPlayer1Up = true;
                isPlayer1Down = isPlayer1Left = isPlayer1Right = false;
                break;
            case KeyEvent.VK_DOWN:
                if (!isPause)
                    isPlayer1Down = true;
                isPlayer1Up = isPlayer1Left = isPlayer1Right = false;
                break;
            case KeyEvent.VK_LEFT:
                if (!isPause)
                    isPlayer1Left = true;
                isPlayer1Up = isPlayer1Down = isPlayer1Right = false;
                break;
            case KeyEvent.VK_RIGHT:
                if (!isPause)
                    isPlayer1Right = true;
                isPlayer1Up = isPlayer1Down = isPlayer1Left = false;
                break;
            case KeyEvent.VK_SPACE, KeyEvent.VK_P:
                isPause = !isPause;
                break;
            case KeyEvent.VK_DELETE:
                isReset = true;
                isPlayer1Up = isPlayer1Down = isPlayer1Left = isPlayer1Right = isPause = false;
                break;
        }

        if ((e.getKeyCode() >= 65 && e.getKeyCode() <= 90) || (e.getKeyCode() >= 97 && e.getKeyCode() <= 122))
            if (!isPause)
                if (c <= 4) {
                    keyTyped = keyTyped.concat(String.valueOf(e.getKeyChar()));
                    c++;
                }
        if (c == 4) {
            for (String s : cheats)
                if (s.equalsIgnoreCase(keyTyped)) {
                    switch (s) {
                        case "swax":
                            cheatType = 's';
                            break;
                        case "life":
                            cheatType = 'l';
                            break;
                        case "dead":
                            cheatType = 'd';
                            break;
                        case "resh":
                            cheatType = 'r';
                            break;
                        case "rest":
                            cheatType = 'R';
                            break;
                        case "move":
                            cheatType = 'm';
                            break;
                        case "save":
                            cheatType = 'S';
                            break;
                    }
                    break;
                }
            c = 0;
            keyTyped = "";
            System.gc();
        }
    }

    public void player2KeyPad(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                if (!isPause)
                    isPlayer2Up = true;
                isPlayer2Down = isPlayer2Left = isPlayer2Right = false;
                break;
            case KeyEvent.VK_S:
                if (!isPause)
                    isPlayer2Down = true;
                isPlayer2Up = isPlayer2Left = isPlayer2Right = false;
                break;
            case KeyEvent.VK_A:
                if (!isPause)
                    isPlayer2Left = true;
                isPlayer2Up = isPlayer2Down = isPlayer2Right = false;
                break;
            case KeyEvent.VK_D:
                if (!isPause)
                    isPlayer2Right = true;
                isPlayer2Up = isPlayer2Down = isPlayer2Left = false;
                break;
        }
    }
}
