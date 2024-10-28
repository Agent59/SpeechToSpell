package net.agent59.spell.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agent59.spell.SpellState;
import net.agent59.spell.SpellTypes;
import net.agent59.spell.util.TargetUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.function.Predicate;

/**
 * Spawns a {@link EntityType#LIGHTNING_BOLT} {@link #everyNTicks} for a specific {@link #targetType}
 * either at what is focused by the casting entity or a random position ({@link #randomTarget}).
 * <p>The target must be within the {@link #range} and have no block above it.
 * <p>If the target is a {@link #randomTarget} it must not be within 8 blocks of the casting entity.
 * <p>The {@link #targetType} may be any of the {@link TargetUtil}'s types.
 * @see TargetUtil
 */
public class LightningSpell extends Spell {
    public static final Codec<LightningSpell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BaseConfiguration.MAP_CODEC.forGetter(Spell::getBaseConf),
            Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(spell -> spell.range),
            Codecs.NONNEGATIVE_INT.fieldOf("every_n_ticks").forGetter(spell -> spell.everyNTicks),
            TargetUtil.CODEC.fieldOf("target_type").forGetter(spell -> spell.targetType),
            Codec.BOOL.optionalFieldOf("random_target", false).forGetter(spell -> spell.randomTarget)
    ).apply(instance, LightningSpell::new));

    public final int range;
    public final int everyNTicks;
    public final TargetUtil targetType;
    /**
     * Ignored when the target is {@link TargetUtil#SELF}.
     */
    public final boolean randomTarget;

    public LightningSpell(
            BaseConfiguration baseConfiguration,
            int range, int everyNTicks, TargetUtil targetType, boolean randomTarget
    ) {
        super(baseConfiguration);
        this.range = range;
        this.everyNTicks = everyNTicks;
        this.targetType = targetType;
        this.randomTarget = randomTarget;
    }

    @Override
    public SpellTypes.SpellType<? extends Spell> getType() {
        return SpellTypes.LIGHTNING;
    }

    @Override
    public boolean tickCasting(SpellState state) {
        if (this.shouldSkipThisTick(state.getRemainingCastingTicks())) return false;

        LivingEntity caster = state.getEntity();
        World world = caster.getWorld();

        // Ensures there is no block above the target.
        // We go one block up, otherwise we would check inside the block.
        Predicate<HitResult> predicate = hit -> world.isSkyVisible(TargetUtil.getBlockPos(hit).up());

        // The predicate is not needed for random targets, as we can just set the needsClearSky argument to true.
        HitResult targetHit = this.randomTarget ?
                this.targetType.getRandomTarget(true, caster, 8, this.range, e -> true, 20) :
                this.targetType.getFocusedTarget(caster, this.range, predicate, false);

        if (targetHit.getType() == HitResult.Type.MISS) return false;

        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
        if (lightningEntity == null) return false; // If the lightning entity could not be created.
        lightningEntity.refreshPositionAfterTeleport(targetHit.getPos());
        world.spawnEntity(lightningEntity);

        return false;
    }

    private boolean shouldSkipThisTick(int remainingTicks) {
        return (this.getCastingTime() - remainingTicks) % this.everyNTicks != 0;
    }
}
