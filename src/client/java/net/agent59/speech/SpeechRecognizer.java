package net.agent59.speech;

import net.agent59.StSMain;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vosk.LibVosk;
import org.vosk.LogLevel;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

/**
 * Controls the recognition thread.
 * <p>From here the speech recognition can be stopped (paused), started (continued) and de-/activated.
 * @see RecognitionThread
 */
public class SpeechRecognizer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String RESOURCE_MODEL_PATH = "./assets/" + StSMain.MOD_ID + "/speech/";
    public static final String RESOURCE_MODEL_NAME = "vosk-model-small-eo-0.42";

    /**
     * Also used to control the {@link RecognitionThread} (volatile for syncing).
     */
    private static volatile State state = State.DEACTIVATED;
    private static Thread recognitionThread;

    /**
     * Registers event-listeners for the speech recognition and sets up Vosk's log-level.
     */
    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> client.execute(() -> activate(client.player)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(SpeechRecognizer::deactivate));
        InvalidateRenderStateCallback.EVENT.register(SpeechRecognizer::reload);

        // Without this, some characters are displayed incorrectly, see https://github.com/alphacep/vosk-api/issues/1449
        System.setProperty("jna.encoding", "UTF-8");

        LogLevel voskLogLevel = FabricLoader.getInstance().isDevelopmentEnvironment() ? LogLevel.DEBUG : LogLevel.INFO;
        LibVosk.setLogLevel(voskLogLevel);

        StSMain.LOGGER.info("Initializing the speech recognizer");
    }

    /**
     * Creates and starts the {@link RecognitionThread}.
     * <p>Called when joining a world / server.
     * @param player The player the speech recognition is made for.
     * @see RecognitionThread#RecognitionThread(ClientPlayerEntity, int, int, String)
     */
    public static void activate(ClientPlayerEntity player) {
        if (state != State.DEACTIVATED) throw new IllegalStateException("Cannot activate SpeechRecognizer that is in state: " + state);

        // TODO read this from a config
        int sampleRate = 16000;
        int cacheSize = 1024;
        String acousticModelPath = FabricLoader.getInstance().getModContainer(StSMain.MOD_ID).get()
                .findPath(RESOURCE_MODEL_PATH + RESOURCE_MODEL_NAME).get().toString();

        try {
            recognitionThread = new Thread(
                    new RecognitionThread(player, sampleRate, cacheSize, acousticModelPath),
                    RecognitionThread.THREAD_NAME
            );
        } catch (IOException | LineUnavailableException e) {
            LOGGER.error("Could not initialize microphone or Vosk's speech recognizer, " +
                    "due to the following error:\n", e);
            return;
        }
        state = State.ACTIVATED;
        recognitionThread.start();
        LOGGER.info("Activated the speech recognition");
    }

    /**
     * Ends the {@link RecognitionThread}. To use speech recognition again, it has to be activated.
     * <p>Called when leaving a world / server.
     */
    public static void deactivate() {
        if (state == State.DEACTIVATED) return;
        state = State.DEACTIVATED;
        try {
            recognitionThread.join(100);
        } catch (InterruptedException e) {
            recognitionThread.interrupt();
        }
        recognitionThread = null;
        LOGGER.info("Deactivated the speech recognition");
    }

    /**
     * Re-instantiates the {@link RecognitionThread}.
     * <p>Called when reloading the world renderer (e.g. by pressing F3).
     * <p>The player must be in a world and the SpeechRecognizer must have been active before the reload.
     */
    public static void reload() {
        if (state == State.DEACTIVATED) return;
        LOGGER.info("Reloading the speech recognition");
        deactivate();
        activate(MinecraftClient.getInstance().player);
    }

    /**
     * Starts or resumes the recognition process after the recognition thread is already running.
     * <p>Should only be called when in a world.
     * @see RecognitionThread#startListening()
     */
    public static void startRecognition() {
        if (state != State.ACTIVATED) {
            LOGGER.error("Can't start / resume recognition while in state {}", state);
            return;
        }
        state = State.STARTING;
        LOGGER.info("Starting speech recognition");
    }

    /**
     * Stops the speech recognition, by pausing the microphone and recognition.
     * <p>Should only be called when in a world.
     * <p>Does not end the recognition thread, which will continue running.
     * @see RecognitionThread#stopListening()
     */
    public static void stopRecognition() {
        if (state == State.DEACTIVATED) return;
        state = State.STOPPING;
        LOGGER.info("Stopped speech recognition");
    }

    /**
     * Should only be called by the {@link RecognitionThread}.
     */
    protected static void setState(State newState) {
        state = newState;
    }

    public static State getState() {
        return state;
    }

    /**
     * Represents the state the speech recognition (thread) is in.
     * <p>The {@link #state} controls the behaviour of the {@link RecognitionThread}.
     * <p>Activated also represents a ready / idle state, from where the speech recognition can be started / resumed.
     */
    public enum State {
        ACTIVATED, // Also meaning: Ready to start recognizing.
        DEACTIVATED,
        RECOGNIZING,
        STARTING,
        STOPPING
    }
}
