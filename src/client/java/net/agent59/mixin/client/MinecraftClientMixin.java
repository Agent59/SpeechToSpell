package net.agent59.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO switch to using events instead!

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Unique
    private static boolean inGame = false;

    // creates a speech thread when joining a world
    @Inject(method = "joinWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setWorld(Lnet/minecraft/client/world/ClientWorld;)V", shift = At.Shift.AFTER))
    public void injectJoinWorld(ClientWorld world, CallbackInfo ci) {

        /*Thread thread = new Thread(speechThread);
        thread.start();*/
        inGame = true;
    }

    // ends the speech thread when leaving a world
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void injectDisconnect(Screen screen, CallbackInfo ci) {
        if (inGame) {

        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    public void resumeSpeechRecognition(CallbackInfo ci) {

    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.AFTER))
    public void pauseSpeechRecognition(CallbackInfo ci) {

    }
}
