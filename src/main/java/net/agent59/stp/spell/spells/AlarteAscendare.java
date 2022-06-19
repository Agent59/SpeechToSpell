package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class AlarteAscendare extends Item implements SpellInterface {
    private static final String NAME = "Alarte Ascendare";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final float RAY_SPEED = 2F;
    private static final int MAX_LIFETIME = 1000;
    private static final int BOOST_HEIGHT = 1;

    public AlarteAscendare(Item.Settings settings) {
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
        RayEntity ray = new RayEntity(ModEntities.ALARTE_ASCENDARE_RAY, world) {

            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity instanceof PlayerEntity) {
                    ServerPlayerEntity player1 = (ServerPlayerEntity) entity;
                    player1.addVelocity(0, BOOST_HEIGHT, 0);
                    player1.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player1));
                }
                this.remove(RemovalReason.DISCARDED);
            }
        };
        ray.configureEntity(player, MAX_LIFETIME, SPELLTYPE, NAME);
        ray.updatePositionAndAngles(player);
        ray.setVelocity(player, RAY_SPEED, 0);
        world.spawnEntity(ray);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
