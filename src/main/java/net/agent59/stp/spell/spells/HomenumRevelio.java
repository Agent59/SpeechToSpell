package net.agent59.stp.spell.spells;

import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class HomenumRevelio extends Item implements SpellInterface {
    private static final String NAME = "Homenum Revelio";
    private static final int RANGE = 25;
    private static final int CASTING_COOLDOWN = 500;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final int DURATION = 100;

    public HomenumRevelio(Settings settings) {
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
        Box box = player.getBoundingBox().expand(RANGE);
        List<Entity> list = world.getOtherEntities(null, box);

        for (Entity entity: list) {
            if (entity instanceof LivingEntity && !(entity.getId() == player.getId()))
                ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, DURATION), player);
        }
        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
