package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class Stupefy extends Item implements SpellInterface {
    private static final String NAME = "Stupefy";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 100;

    private static final float RAY_SPEED = 0.1F;

    public Stupefy(Settings settings) {
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
    public void execute(ServerPlayerEntity player) {
        World world = player.getWorld();

        //RayEntity stupefyRay = new RayEntity(ModEntities.STUPEFY_RAY, world);
        RayEntity stupefyRay = new RayEntity(ModEntities.STUPEFY_RAY, world);
        stupefyRay.refreshPositionAndAngles(player);
        stupefyRay.setVelocity(player, RAY_SPEED, 0);

        world.spawnEntity(stupefyRay);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
