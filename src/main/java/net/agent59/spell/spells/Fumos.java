package net.agent59.spell.spells;

import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class Fumos extends Item implements SpellInterface {
    private static final String NAME = "Fumos";
    private static final int RANGE = 3;
    private static final int CASTING_COOLDOWN = 200;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final int PARTICLES_PER_BLOCK = 20;

    public Fumos(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Creates a cloud of smoke around you.";
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
        ServerWorld world = player.getServerWorld();

        for (int r = 0; r < (RANGE * 2); ++r) {
            for (int c = 0; c < (RANGE * 2); ++c) {
                for (int h = 0; h < (RANGE * 2); ++h) {
                    double x = player.getX() + r - RANGE;
                    double y = player.getEyeY() + h - RANGE;
                    double z = player.getZ() + c - RANGE;

                    for (int l = 0; l < PARTICLES_PER_BLOCK; ++l) {
                        world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                x + Math.random(), y + Math.random(), z + Math.random(),
                                1, 0, 0, 0, 0);
                    }
                }
            }
        }
        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
