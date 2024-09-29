package net.agent59.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agent59.StSMain;
import net.agent59.cardinal_component.ClientPlayerMagicComponent;
import net.agent59.spell.SpellState;
import net.agent59.spell.spells.Spell;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

/**
 * Renders the spell-hotbar and the spells, that are cooling down, into the hud.
 */
public class MagicHud {
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    public static final int COOLDOWN_COLOR = 1354372620; // ColorHelper.Argb.getArgb(80, 186, 22, 12)
    public static final int CASTING_COLOR = 1344962560; // ColorHelper.Argb.getArgb(80, 42, 128, 0)

    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            Window window = MinecraftClient.getInstance().getWindow();
            int hudHeight = window.getScaledHeight();

            renderSpellHotbar(drawContext, hudHeight, player);
            renderSpellsCoolingDown(drawContext, player);
        });
        StSMain.LOGGER.info("Initializing the MagicHud");
    }

    private static void renderSpellHotbar(DrawContext context, int hudHeight, ClientPlayerEntity player) {
        ClientPlayerMagicComponent playerMagicComp = ClientPlayerMagicComponent.getInstance(player);
        Spell[] slots = playerMagicComp.getSpellHotbar();
        if (slots.length == 0) return;

        int hotbarY = hudHeight / 2 - slots.length * 10 + 1;
        int hotbarX = -1;

        int currentSlotY = hotbarY;
        for (Spell spell : slots) {
            // hotbar-slot
            context.getMatrices().push();
            RenderSystem.enableBlend();
            context.drawTexture(WIDGETS_TEXTURE, hotbarX, currentSlotY, 24, 23, 22, 22);
            RenderSystem.disableBlend();
            context.getMatrices().pop();
            // spell in current the slot
            if (spell != null) renderSpell(context, player, hotbarX + 3, currentSlotY + 3, spell);

            currentSlotY += 21;
        }

        // selected slot border
        int selectedSlot = playerMagicComp.getSelectedSpellHotbarSlot();
        context.getMatrices().push();
        context.drawTexture(WIDGETS_TEXTURE, hotbarX, hotbarY + 21 * selectedSlot, 1, 23, 22, 22);
        context.getMatrices().pop();
    }

    private static void renderSpellsCoolingDown(DrawContext context, ClientPlayerEntity player) {
        ClientPlayerMagicComponent playerMagicComp = ClientPlayerMagicComponent.getInstance(player);
        Spell[] spellHotbar = playerMagicComp.getSpellHotbar();
        int currentX = 3;
        for (Spell spell : playerMagicComp.getSpellsCoolingDown()) {
            if (Arrays.stream(spellHotbar).noneMatch(spell::equals)) {
                renderSpell(context, player, currentX, 3, spell);
                currentX += 20;
            }
        }
    }

    private static void renderSpell(DrawContext context, ClientPlayerEntity player, int x, int y, Spell spell) {
        context.drawItem(spell.getDisplayIcon().getDefaultStack(), x, y);

        ClientPlayerMagicComponent playerMagicComp = ClientPlayerMagicComponent.getInstance(player);
        SpellState state = playerMagicComp.getSpellsState(spell);
        float f;
        int color;
        if (state.getRemainingCooldown() != 0) {
            f = (float) state.getRemainingCooldown() / spell.getCooldownTime();
            color = COOLDOWN_COLOR;
        } else if (state.getRemainingCastingTicks() != 0) {
            f = (float) state.getRemainingCastingTicks() / spell.getCastingTime();
            color = CASTING_COLOR;
        } else return;

        int k = y + MathHelper.floor(16.0F * (1.0F - f));
        int l = k + MathHelper.ceil(16.0F * f);
        context.getMatrices().push();
        context.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, color);
        context.getMatrices().pop();
    }
}
