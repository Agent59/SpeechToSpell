package net.agent59.stp.mixin;

import net.agent59.stp.speech.Sphinx4SpeechThread;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    private static boolean inGame = false;

    // creates a speech thread when joining a world
    @Inject(method = "joinWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setWorld(Lnet/minecraft/client/world/ClientWorld;)V", shift = At.Shift.AFTER))
    public void injectJoinWorld(ClientWorld world, CallbackInfo ci) {
        System.out.println("injectJoinWorld()");

        Sphinx4SpeechThread speechThread = Sphinx4SpeechThread.getInstance();
        Thread thread = new Thread(speechThread);
        thread.start();
        inGame = true;
    }

    // ends the speech thread when leaving a world
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void injectDisconnect(Screen screen, CallbackInfo ci) {
        if (inGame) {
            System.out.println("injectDisconnect()");
            Sphinx4SpeechThread.getInstance().end();
        }
    }
}
