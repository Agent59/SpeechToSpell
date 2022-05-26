package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;


public class Protego extends Item implements SpellInterface {
    private static final String NAME = "Protego";
    private static final int RANGE = 2;
    private static final int CASTING_COOLDOWN = 25;
    private static final int MAX_LIVETIME = 30;

    public Protego(Settings settings) {
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
        ServerWorld world = player.getWorld();

        RayEntity protegoWall = new RayEntity(ModEntities.PROTEGO_WALL, world) {
            private int age = 0;

            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity instanceof RayEntity) {
                    entity.kill();
                    this.kill();
                }
            }

            @Override
            public void tick() {
                super.tick();
                ++this.age;
                if (this.age >= MAX_LIVETIME) {
                    this.kill();
                }
            }
        };
        protegoWall.setOwner(player);
        HitResult hitResult = player.raycast(RANGE, 0, false);
        protegoWall.setPosition(hitResult.getPos().getX(), hitResult.getPos().getY() -1, hitResult.getPos().getZ());
        protegoWall.setYaw(player.getYaw());

        world.spawnEntity(protegoWall);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
