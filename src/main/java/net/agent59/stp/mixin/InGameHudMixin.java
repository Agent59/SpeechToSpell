package net.agent59.stp.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agent59.stp.Main;
import net.agent59.stp.item.custom.WandItem;
import net.agent59.stp.spell.SpellHandler;
import net.agent59.stp.spell.SpellInterface;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

// injects into the InGameHud and inserts the spell-hotbar to be rendered

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int scaledHeight;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void init(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        ItemStack wand;
        // if the player is holding a wand
        if (((wand = player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = player.getOffHandStack()).getItem() instanceof WandItem)) {
            this.renderSpellHotbar(wand, player, matrices, tickDelta);
        }
    }

    private void renderSpellHotbar(ItemStack wand, PlayerEntity playerEntity, MatrixStack matrices, float tickDelta) {
        if (playerEntity != null) {

            // hotbar
            Identifier SPELLHOTBAR_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/spell_hotbar.png");
            RenderSystem.setShaderTexture(0, SPELLHOTBAR_TEXTURE);

            int hotbarY = this.scaledHeight / 2 - 51;
            int hotbarX = 0;
            this.drawTexture(matrices, hotbarX, hotbarY, 0, 0, 22, 102);

            //selected slot
            assert wand.getNbt() != null;
            int selectedSlot = wand.getNbt().getInt(Main.MOD_ID + ".spellHotbarSelectedSlot");
            this.drawTexture(matrices, hotbarX - 1, (hotbarY - 1) + 20 * (selectedSlot - 1), 22, 0, 24, 24);


            // spell-icons
            for (int i = 1; i <= 5 ; i++){
                if (wand.getNbt() != null) {
                    String spellName = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + i);
                    if (!Objects.equals(spellName, "")) {

                        SpellInterface spell = SpellHandler.getSpellNameHashmap().get(spellName);

                        Identifier spellIcon_Texture = spell.getIconIdentifier();
                        RenderSystem.setShaderTexture(0, spellIcon_Texture);

                        this.drawTexture(matrices, hotbarX + 3, (hotbarY + 3) + 20 * (i - 1), 0, 0, 16, 16);
                    }
                }
            }
        }
    }
}