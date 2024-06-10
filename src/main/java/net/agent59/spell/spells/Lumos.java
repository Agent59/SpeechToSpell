package net.agent59.spell.spells;

import net.agent59.entity.ModEntities;
import net.agent59.entity.custom.LumosEntity;
import net.agent59.entity.custom.RayEntity;
import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class Lumos extends Item implements SpellInterface {
    private static final String NAME = "Lumos";
    private static final int RANGE = 15;
    private static final int CASTING_COOLDOWN = 100;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final int MAX_LIFETIME = 2400;

    public Lumos(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "A light source will follow you through the air.";
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
        RayEntity ray = new LumosEntity(ModEntities.LUMOS_ORB, world);
        ray.configureEntity(player, MAX_LIFETIME, SPELLTYPE, NAME);
        ray.updatePositionAndAngles(player);
        world.spawnEntity(ray);
    }
}
