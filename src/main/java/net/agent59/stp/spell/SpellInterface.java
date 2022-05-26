package net.agent59.stp.spell;

import net.minecraft.item.ItemConvertible;
import net.minecraft.server.network.ServerPlayerEntity;

public interface SpellInterface extends ItemConvertible {
    String getStringName();
    int getRange();
    int getCastingCooldown();
    SpellType getSpellType();

    void execute(ServerPlayerEntity player);
}