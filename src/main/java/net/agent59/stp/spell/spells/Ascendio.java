package net.agent59.stp.spell.spells;

import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class Ascendio extends Item implements SpellInterface {
    private static final String NAME = "Ascendio";
    private static final int RANGE = 0;
    private static final int CASTING_COOLDOWN = 40;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final int BOOST_HEIGHT = 1;

    public Ascendio(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public int getCastingCooldown() {
        return CASTING_COOLDOWN;
    }

    @Override
    public SpellType getSpellType() {
        return SPELLTYPE;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        player.addVelocity(0, BOOST_HEIGHT, 0);
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
