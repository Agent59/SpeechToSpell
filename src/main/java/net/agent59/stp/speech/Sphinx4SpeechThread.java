package net.agent59.stp.speech;

import edu.cmu.sphinx.api.SpeechResult;
import net.agent59.stp.Main;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Arrays;

public class Sphinx4SpeechThread implements Runnable {

    // other values for audio format won't work (https://cmusphinx.github.io/wiki/tutorialsphinx4/#streamspeechrecognizer)
    private final AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
    private final TargetDataLine mic = AudioSystem.getTargetDataLine(format);
    private final AudioInputStream inputStream = new AudioInputStream(mic);
    private CustomStreamSpeechRecognizer recognizer;
    private final PlayerEntity user;

    public Sphinx4SpeechThread(PlayerEntity user) throws LineUnavailableException {
        this.user = user;
    }

    @Override
    public void run() {
        try {
            recognizer = new CustomStreamSpeechRecognizer(Sphinx4Conf.returnConf());
            mic.open();

            // This fixes the accumulating audio issue on some Linux systems
            mic.start();
            mic.stop();
            mic.flush();

            mic.start();
            recognizer.startRecognition(inputStream);

            SpeechResult speechResult;
            while ((speechResult = recognizer.getResult()) != null) {
                String voice_command = speechResult.getHypothesis();
                // voice_command is upperCase, so it has to be converted to titelCase
                String spellString = voice_command.charAt(0) + voice_command.substring(1).toLowerCase();

                Main.LOGGER.info("Spell is: " + spellString);
                this.user.sendMessage(new LiteralText(spellString), true);

                //create the packet for the spell to send to the server
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(spellString);
                //send the packaged spell to the server
                ClientPlayNetworking.send(new Identifier(Main.MOD_ID, "spell"), buf);
            }

        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            Main.LOGGER.info("THE FOLLOWING EXCEPTION WAS CAUSED WHILE STOPPING THE SPEECH THREAD" +
                    " AND WAS PROBABLY INTENDED:\n\t" + Arrays.toString(e.getStackTrace()));
        }
        Main.LOGGER.info("SPEECH THREAD ENDING");
    }

    public void end(){
        try {
            inputStream.close();
            mic.stop();
            mic.flush();
            recognizer.cancelRecognition();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
