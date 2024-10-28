package net.agent59.spell.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agent59.StSMain;
import net.agent59.spell.SpellState;
import net.agent59.spell.SpellTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Box;

import java.util.function.Predicate;

/**
 * Lets the caster collect things ({@link #collect_items}, {@link #collect_xp}, {@link #collect_projectiles}),
 * laying in the {@link #range}.
 * <p>At the moment this spell only has an effect when a player casts it.
 */
public class CollectSpell extends Spell {
    public static final Codec<CollectSpell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BaseConfiguration.MAP_CODEC.forGetter(Spell::getBaseConf),
            Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(spell -> spell.range),
            Codec.BOOL.fieldOf("collect_items").forGetter(spell -> spell.collect_items),
            Codec.BOOL.fieldOf("collect_xp").forGetter(spell -> spell.collect_xp),
            Codec.BOOL.fieldOf("collect_projectiles").forGetter(spell -> spell.collect_projectiles)
    ).apply(instance, CollectSpell::new));

    private final int range;
    private final boolean collect_items;
    private final boolean collect_xp;
    private final boolean collect_projectiles;

    public CollectSpell(
            BaseConfiguration baseConfiguration,
            int range, boolean collect_items, boolean collect_xp, boolean collect_projectiles
    ) {
        super(baseConfiguration);
        this.range = range;
        this.collect_items = collect_items;
        this.collect_xp = collect_xp;
        this.collect_projectiles = collect_projectiles;
    }

    @Override
    public SpellTypes.SpellType<? extends Spell> getType() {
        return SpellTypes.COLLECT;
    }

    @Override
    public boolean tickCasting(SpellState state) {
        LivingEntity caster = state.getEntity();
        Box box = caster.getBoundingBox().expand(this.range);

        Predicate<Entity> canPickup = e -> false;
        if (this.collect_items) canPickup = e -> e instanceof ItemEntity;
        if (this.collect_xp) canPickup = canPickup.or(e -> e instanceof ExperienceOrbEntity);
        if (this.collect_projectiles) canPickup = canPickup.or(e -> e instanceof ProjectileEntity);

        for (Entity entityInRange : caster.getWorld().getOtherEntities(null, box, canPickup)) {
            StSMain.LOGGER.info("entity: {}", entityInRange);
            if (caster instanceof PlayerEntity) entityInRange.onPlayerCollision((PlayerEntity) caster);
            // TODO allow for non-player casters to pick up items.
        }
        return false;
    }
}
