package net.agent59.stp.speech;

import edu.cmu.sphinx.api.SpeechResult;
import net.agent59.stp.Main;
import net.agent59.stp.item.custom.WandItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class Sphinx4SpeechThread implements Runnable{

    public volatile boolean listening;

    PlayerEntity user;

    public Sphinx4SpeechThread(PlayerEntity user) {
        this.user = user;
    }

    @Override
    public void run() {
        this.listening = true;
        CustomLiveSpeechRecognizer recognizer = null;
        try {
            recognizer = new CustomLiveSpeechRecognizer(Sphinx4Conf.returnConf());
            // Start recognition process pruning previously cached data.
            recognizer.startRecognition(true);

            System.out.println("started speech Thread");

            SpeechResult speechResult;

            while (listening) {

                if (!(this.user.getActiveItem().getItem() instanceof WandItem)) {
                    this.user.sendMessage(new LiteralText("No active WandItem in Hand"), true);
                    listening = false;
                }

                else if ((speechResult = recognizer.getResult()) != null && listening &&
                        this.user.getActiveItem().getItem() instanceof WandItem) {
                    String voice_command = speechResult.getHypothesis();
                    // voice_command is upperCase, so it has to be converted to titelCase
                    String spellString = voice_command.charAt(0) + voice_command.substring(1).toLowerCase();

                    System.out.println("Spell is: " + spellString);
                    this.user.sendMessage(new LiteralText(spellString), true);

                    //create the packet for the spell to send to the server
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeString(spellString);
                    //send the packaged spell to the server
                    ClientPlayNetworking.send(new Identifier(Main.MOD_ID, "spell"), buf);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        assert recognizer != null;
        recognizer.stopRecognition();
        System.out.println("STOPPED SPEECH THREAD");
    }
}