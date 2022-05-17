package net.agent59.stp.spell;

import net.minecraft.item.ItemConvertible;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface SpellInterface extends ItemConvertible {
    String getStringName();
    Identifier getIconIdentifier();
    int getRange();
    int getCastingCooldown();

    void execute(ServerPlayerEntity player);
}
