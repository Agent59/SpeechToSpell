package net.agent59.stp.spell.spells;

import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Apparate extends Item implements SpellInterface {
    private static final String NAME = "Apparate";
    private static final int RANGE = 250;
    private static final int CASTING_COOLDOWN = 250;
    private static final SpellType SPELLTYPE = SpellType.JINX;
    private static final int BOX_SIZE = 2;

    public Apparate(Settings settings) {
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

        HitResult hitResult = player.getCameraEntity().raycast(RANGE, 0, true);
        if (hitResult.getType() != HitResult.Type.MISS) {
            ServerWorld world = player.getWorld();
            Vec3d pos = null;

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult)hitResult;
                Direction direction = blockHitResult.getSide();

                pos = blockHitResult.getPos().add(direction.getOffsetX(), direction.getOffsetY() - 1,
                        direction.getOffsetZ());

            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                pos = hitResult.getPos();
            }
            assert pos != null;

            // teleports all players that are in the expanded box of the player
            Box box = player.getBoundingBox().expand(BOX_SIZE);
            List<Entity> list = world.getOtherEntities(null, box);

            for (Entity entity: list) {
                if (entity instanceof PlayerEntity)
                    entity.teleport(pos.getX(), pos.getY(), pos.getZ());
            }
            //set cooldown
            player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
        }
    }
}
