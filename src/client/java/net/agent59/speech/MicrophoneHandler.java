package net.agent59.speech;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Handles a data-line that represents a microphone, which the {@link SpeechRecognizer} uses.
 */
public class MicrophoneHandler {
    private final TargetDataLine line;
    private final int cacheSize;

    public MicrophoneHandler(int sampleRate, int cacheSize) throws Exception {
        this.cacheSize = cacheSize;
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        this.line = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, format));
        this.line.open(format);

        this.line.start();
        this.line.stop();
        this.line.flush();
    }

    public void startListening() {
        this.line.flush();
        this.line.start();
    }

    public void stopListening() {
        this.line.stop();
    }

    public void end() {
        this.line.close();
    }

    public byte[] readData() {
        byte[] buffer = new byte[this.cacheSize];
        int count = this.line.read(buffer, 0, buffer.length);

        if (count > 0) return buffer;
        else return new byte[0];
    }
}
