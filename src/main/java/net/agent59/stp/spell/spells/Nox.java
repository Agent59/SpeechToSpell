package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class Nox extends Item implements SpellInterface {
    private static final String NAME = "Nox";
    private static final int RANGE = 5;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.CHARM;

    public Nox(Settings settings) {
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
        World world = player.getWorld();
        Box box = player.getBoundingBox().expand(RANGE);
        List<Entity> list = world.getOtherEntities(null, box);

        for (Entity entity: list) {
            if (entity.getType() == ModEntities.LUMOS_ORB) {
                entity.discard();
                //set cooldown
                player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
            }
        }
    }
}
