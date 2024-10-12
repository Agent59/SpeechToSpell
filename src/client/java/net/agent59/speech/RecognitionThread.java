package net.agent59.speech;

import com.google.gson.JsonParser;
import net.agent59.cardinal_component.ClientPlayerMagicComponent;
import net.agent59.speech.SpeechRecognizer.State;
import net.agent59.spell.SpellManager;
import net.agent59.spell.spells.Spell;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

/**
 * A runnable that recognizes speech and casts recognized spells.
 * <p>Is controlled via the {@link State} held by {@link SpeechRecognizer#state}:
 * <ul>
 *     <li>{@link State#STARTING} causes the thread to pick up (or resume) the recognition,
 *     after which the state is changed to {@link State#RECOGNIZING}.</li>
 *     <li>{@link State#STOPPING} causes the thread to halt / pause the recognition (can be resumed),
 *     after which the state is changed {@link State#ACTIVATED}.</li>
 *     <li>{@link State#DEACTIVATED} causes the thread to end its loop and cleanup the recognizer and microphone,
 *     from here a new thread would need to instantiated to recognize again.</li>
 * </ul>
 * @see #run()
 * @see Recognizer
 * @see MicrophoneHandler
 * @see SpeechRecognizer
 */
public class RecognitionThread implements Runnable {
    public static final String THREAD_NAME = "SpeechRecognition thread";

    private final Recognizer recognizer;
    private final MicrophoneHandler mic;
    private final ClientPlayerEntity player;

    public RecognitionThread(ClientPlayerEntity player, int sampleRate, int cacheSize, String acousticModelPath) throws IOException, LineUnavailableException {
        SpeechRecognizer.LOGGER.info("Looking for the speech model in path: {}", acousticModelPath);
        this.mic = new MicrophoneHandler(sampleRate, cacheSize); // Could throw a LineUnavailableException.
        this.recognizer = new Recognizer(new Model(acousticModelPath), sampleRate); // Could throw an IOException.
        this.player = player;
    }

    /**
     * @see SpeechRecognizer#startRecognition()
     */
    private void startListening() {
        mic.startListening();
        SpeechRecognizer.setState(State.RECOGNIZING);
    }

    /**
     * @see SpeechRecognizer#stopRecognition()
     */
    private void stopListening() {
        mic.stopListening();
        recognizer.reset();
        SpeechRecognizer.setState(State.ACTIVATED);
    }

    private void recognize() {
        byte[] data = mic.readData();
        if (recognizer.acceptWaveForm(data, data.length)) {
            String text = JsonParser.parseString(recognizer.getResult()).getAsJsonObject().get("text").getAsString();

            SpeechRecognizer.LOGGER.info("Recognized: {}", text);
            player.sendMessage(Text.of(text), true);

            Spell spell = SpellManager.getSpell(text);
            if (spell != null) ClientPlayerMagicComponent.getInstance(player).castSpell(spell, false);
        }
    }

    @Override
    public void run() {
        State state;
        while ((state = SpeechRecognizer.getState()) != State.DEACTIVATED) {
            switch (state) {
                case STARTING -> this.startListening();
                case STOPPING -> this.stopListening();
                case RECOGNIZING -> this.recognize();
            }
        }
        mic.end();
        recognizer.close();
    }
}
