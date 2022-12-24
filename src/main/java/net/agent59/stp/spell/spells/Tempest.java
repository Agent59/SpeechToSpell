package net.agent59.stp.spell.spells;

import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Tempest extends Item implements SpellInterface {
    private static final String NAME = "Tempest";
    private static final int RANGE = 32;
    private static final int CASTING_COOLDOWN = 250;
    private static final SpellType SPELLTYPE = SpellType.JINX;

    public Tempest(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Strikes a random player or mob, that has no block above them, with a lightning.";
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
        list.removeIf(entity -> !(entity instanceof LivingEntity) || entity.getId() == player.getId() || !(world.isSkyVisible(entity.getBlockPos())));

        if (list.size() != 0) {
            LivingEntity livingEntity = (LivingEntity) list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(livingEntity.getBlockPos()));
            world.spawnEntity(lightningEntity);

            //set cooldown
            player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
        }
    }
}
