package net.agent59.command;

import net.agent59.StSMain;
import net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class StSGameRules {

    public static final CustomGameRuleCategory MAGIC =
            new CustomGameRuleCategory(StSMain.id("magic"), Text.translatable("gamerule.category.speech_to_spell.magic"));

    /**
     * Determines how many slots a players spell-hotbar has.
     * @see net.agent59.cardinal_component.player_magic_comp.PlayerMagicComponent#spellHotbar
     */
    public static final GameRules.Key<GameRules.IntRule> SPELL_HOTBAR_SLOTS = GameRuleRegistry.register(
            "spellHotbarSlots", MAGIC, GameRuleFactory.createIntRule(5, 0, (server, intRule) -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    ServerPlayerMagicComponent.getInstance(player).onSpellHotbarSlotsGameruleChange();
                }
            })
    );

    public static void initialize() {
        StSMain.LOGGER.info("Registering gamerule categories and gamerules for " + StSMain.MOD_NAME);
    }
}
