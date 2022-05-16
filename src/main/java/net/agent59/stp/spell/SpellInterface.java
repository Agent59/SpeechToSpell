package net.agent59.stp.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public interface SpellInterface {
    String getName();
    Identifier getIconIdentifier();
    int getRange();
    int getCastingCooldown();

    void execute(PlayerEntity player);
}
