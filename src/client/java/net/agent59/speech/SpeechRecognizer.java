package net.agent59.speech;

import com.google.gson.JsonParser;
import net.agent59.StSMain;
import net.agent59.cardinal_component.ClientPlayerMagicComponent;
import net.agent59.spell.SpellManager;
import net.agent59.spell.spells.Spell;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

/**
 * Handles the speech recognition with which the player can cast spells.
 * @see Recognizer
 * @see MicrophoneHandler
 */
public class SpeechRecognizer {
    public static final String NAME = SpeechRecognizer.class.getSimpleName();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final String RESOURCE_MODEL_PATH = "./assets/" + StSMain.MOD_ID + "/speech/";
    public static final String RESOURCE_MODEL_NAME = "vosk-model-small-eo-0.42";

    private static volatile State state = State.DEACTIVATED;

    private static Recognizer recognizer;
    private static MicrophoneHandler mic;
    private static Thread recognitionThread;
    private static ClientPlayerEntity player;

    /**
     * Registers event-listeners for the speech recognition and sets up Vosk's log-level.
     */
    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> client.execute(() -> activate(client.player)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(SpeechRecognizer::deactivate));

        LogLevel voskLogLevel = FabricLoader.getInstance().isDevelopmentEnvironment() ? LogLevel.DEBUG : LogLevel.INFO;
        LibVosk.setLogLevel(voskLogLevel);

        StSMain.LOGGER.info("Initializing the speech recognizer");
    }

    /**
     * Instantiates the MicrophoneHandler and the Recognizer and starts the listening-thread.
     * <p>Called when joining a world / server.
     * @param clientPlayer The player the speech recognition is made for.
     */
    public static void activate(ClientPlayerEntity clientPlayer) {
        if (state != State.DEACTIVATED) return;
        player = clientPlayer;

        // TODO read this from a config
        int sampleRate = 16000;
        int cacheSize = 1024;
        String acousticModelPath = FabricLoader.getInstance().getModContainer(StSMain.MOD_ID).get()
                .findPath(RESOURCE_MODEL_PATH + RESOURCE_MODEL_NAME).get().toString();
        LOGGER.info("Looking for the speech model in path: {}", acousticModelPath);

        try {
            recognizer = new Recognizer(new Model(acousticModelPath), sampleRate);
            mic = new MicrophoneHandler(sampleRate, cacheSize);
        } catch (Exception e) {
            LOGGER.error("Could not initialize microphone or speech recognizer, " +
                    "due to the following error:\n", e);
            return;
        }
        recognitionThread = new Thread(SpeechRecognizer::listenThreadTask, NAME + " thread");
        recognitionThread.start();
        state = State.ACTIVATED;
        LOGGER.info("Activated speech recognition");
    }

    /**
     * Ends the listening-thread, MicrophoneHandler and the Recognizer.
     * <p>Called when leaving a world / server.
     */
    public static void deactivate() {
        if (state == State.DEACTIVATED) return;
        state = State.DEACTIVATED;
        recognitionThread.interrupt();
        recognitionThread = null;
        mic.end();
        mic = null;
        recognizer.close();
        recognizer = null;
        player = null;
        LOGGER.info("Deactivated speech recognition");
    }

    /**
     * Re-instantiates the Recognizer, the MicrophoneHandler and the listening-thread.
     * <p>Should only be called when in a world.
     * @param player The player that is re-supplied. Should be the same as before.
     */
    public static void reload(ClientPlayerEntity player) {
        deactivate();
        activate(player);
    }

    /**
     * Starts or resumes the recognition process after the listening-thread is already running.
     * <p>Should only be called when in a world.
     */
    public static void startRecognition() {
        if (state != State.ACTIVATED) {
            LOGGER.info("Can't recognize while in state {}", state);
            return;
        }
        mic.startListening();
        state = State.RECOGNIZING;
        LOGGER.info("Started speech recognition");
    }

    /**
     * Stops the speech recognition, by pausing the microphone and recognition.
     * <p>Should only be called when in a world.
     * <p>Does not end the listening-thread, which will continue running.
     */
    public static void stopRecognition() {
        if (state == State.DEACTIVATED) return;
        mic.stopListening();
        state = State.ACTIVATED;
        LOGGER.info("Stopped speech recognition");
    }

    public static State getRecognitionState() {
        return state;
    }

    /**
     * Is run inside the {@link #recognitionThread}.
     * <p>Reads from the microphone during the recognizing state and casts the spells that are recognized.
     * <p>Should only be run when in a world.
     */
    private static void listenThreadTask() {
        while (state != State.DEACTIVATED) {
            if (state != State.RECOGNIZING) continue;
            String text = getStringMsg(mic.readData());
            if (!text.isEmpty()) {
                LOGGER.info("Recognized text: {}", text);
                player.sendMessage(Text.of(text), true);

                Spell spell = SpellManager.getSpell(text);
                if (spell != null) ClientPlayerMagicComponent.getInstance(player).castSpell(spell, false);
            }
        }
    }

    /**
     * @return The text that was recognized based on the given bytes.
     */
    private static String getStringMsg(byte[] data) {
        return recognizer.acceptWaveForm(data, data.length) ?
                JsonParser.parseString(recognizer.getResult()).getAsJsonObject().get("text").getAsString() : "";
    }

    /**
     * Represents the state the speech recognition (thread) is in.
     * <p>Activated also represents a ready / idle state, from where the speech recognition can be started / resumed.
     */
    public enum State {
        ACTIVATED, // Also meaning: Ready to start recognizing.
        DEACTIVATED,
        RECOGNIZING,
    }
}
