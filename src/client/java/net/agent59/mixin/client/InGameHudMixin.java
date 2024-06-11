package net.agent59.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agent59.Main;
import net.agent59.item.custom.WandItem;
import net.agent59.spell.SpellHandler;
import net.agent59.spell.SpellInterface;
import net.agent59.util.UpdateNbt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    // TODO make spellHotbarSlots transparent

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int scaledHeight;

    @Shadow
    protected abstract void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed);

    @Unique
    private static final Identifier SPELLHOTBAR_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/spell_hotbar.png");

    // inserts the spell-hotbar, which is to be rendered, after minecrafts renderHotbar function
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void init(DrawContext context, float tickDelta, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        ItemStack wand;
        // if the player is holding a wand
        if (((wand = player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = player.getOffHandStack()).getItem() instanceof WandItem)) {
            this.renderSpellHotbar(wand, player, tickDelta, context);
        }
    }

    @Unique
    private void renderSpellHotbar(ItemStack wand, PlayerEntity playerEntity, float tickDelta, DrawContext context) {
        if (playerEntity == null) {
            return;
        }

        // hotbar
        RenderSystem.setShaderTexture(0, SPELLHOTBAR_TEXTURE);

        int hotbarY = this.scaledHeight / 2 - 51;
        int hotbarX = 0;
        context.drawTexture(SPELLHOTBAR_TEXTURE, hotbarX, hotbarY, 0, 0, 22, 102);

        if (wand.getNbt() == null) {
            UpdateNbt.updateWandNbtFromClient(".spellHotbarSelectedSlot", null, 1);
            NbtCompound nbt = new NbtCompound();
            nbt.putInt(Main.MOD_ID + ".spellHotbarSelectedSlot", 1);
            wand.setNbt(nbt);
        }
        int selectedSlot = wand.getNbt().getInt(Main.MOD_ID + ".spellHotbarSelectedSlot");
        context.drawTexture(SPELLHOTBAR_TEXTURE, hotbarX - 1, (hotbarY - 1) + 20 * (selectedSlot - 1), 22, 0, 24, 24);

        ArrayList<SpellInterface> spellsInHotbar = new ArrayList<>();
        // spell-icons
        int m = 1;
        for (int i = 1; i <= 5; i++) {
            if (wand.getNbt() != null) {
                String spellName = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + i);
                if (!Objects.equals(spellName, "")) {
                    SpellInterface spell = SpellHandler.getSpellNameHashmap().get(spellName);

                    this.renderHotbarItem(context, hotbarX + 3, (hotbarY + 3) + 20 * (i - 1), tickDelta, playerEntity, spell.asItem().getDefaultStack(), m++);
                    spellsInHotbar.add(spell);
                }
            }
        }

        // spell-cooldown-icons
        int i = 1;
        for (SpellInterface spell: SpellHandler.getSpellList()) {
            boolean isCoolingdown = playerEntity.getItemCooldownManager().isCoolingDown(spell.asItem());

            if (isCoolingdown && !spellsInHotbar.contains(spell)) {
                this.renderHotbarItem(context, 3 + 20 * (i - 1), 3, tickDelta, playerEntity, spell.asItem().getDefaultStack(), i);
                i++;
            }
        }

    }
}
