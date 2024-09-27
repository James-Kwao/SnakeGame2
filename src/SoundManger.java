import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Objects;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class SoundManger {

    private Clip clip;
    private long curPos = 1;

    public SoundManger() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(Objects.requireNonNull(getSystemResourceAsStream("res/sound/music.wav")));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void playMusic() {
        try {
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ignored) {
        }
    }

    public void stopMusic() {
        try {
            clip.stop();
            clip.setMicrosecondPosition(0);
            curPos = 0;
        } catch (Exception ignored) {
        }
    }

    public void exit() {
        try {
            clip.stop();
            curPos = 0;
            clip.close();
        } catch (Exception ignored) {
        }
    }
}
