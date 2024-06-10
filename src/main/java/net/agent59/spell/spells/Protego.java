package net.agent59.spell.spells;

import net.agent59.entity.ModEntities;
import net.agent59.entity.custom.RayEntity;
import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;

import java.util.List;


public class Protego extends Item implements SpellInterface {
    private static final String NAME = "Protego";
    private static final int RANGE = 2;
    private static final int CASTING_COOLDOWN = 25;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final int MAX_LIVETIME = 100;
    private static final int PROTEGO_TYPE = 1;

    public Protego(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Creates a magical wall in front of you that blocks magical rays. When the protego wall is hit, it will be destroyed.";
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

        RayEntity protegoWall = new RayEntity(ModEntities.PROTEGO_WALL, world) {
            @Override
            public void tick2() {
                Box box = this.getBoundingBox();
                List<Entity> list = this.getWorld().getOtherEntities(null, box);

                for (Entity entity: list) {
                    if (entity instanceof RayEntity) {
                        if (((RayEntity) entity).getMinProtegoType() >= PROTEGO_TYPE) {
                            entity.kill();
                            this.kill();
                            break;
                        }
                    }
                }
            }
        };
        protegoWall.configureEntity(player, MAX_LIVETIME, SPELLTYPE, NAME);
        protegoWall.setMinProtegoType(0);
        HitResult hitResult = player.raycast(RANGE, 0, false);
        protegoWall.setPosition(hitResult.getPos().getX(), hitResult.getPos().getY() -1, hitResult.getPos().getZ());
        protegoWall.setYaw(player.getYaw());

        world.spawnEntity(protegoWall);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
