package net.agent59.stp.spell;

import com.google.common.collect.Maps;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.Map;

// this class is based on minecrafts ItemCooldownManager class

public class SpellCooldownManager{
    private final Map<SpellInterface, SpellCooldownManager.Entry> entries = Maps.newHashMap();
    private int tick;

    public boolean isCoolingDown(SpellInterface spell) {
        return this.getCooldownProgress(spell, 0.0F) > 0.0F;
    }

    public float getCooldownProgress(SpellInterface spell, float partialTicks) {
        SpellCooldownManager.Entry entry = (SpellCooldownManager.Entry)this.entries.get(spell);
        if (entry != null) {
            float f = (float)(entry.endTick - entry.startTick);
            float g = (float)entry.endTick - ((float)this.tick + partialTicks);
            return MathHelper.clamp(g / f, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void update() {
        ++this.tick;
        if (!this.entries.isEmpty()) {
            Iterator iterator = this.entries.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                if (((SpellCooldownManager.Entry)entry.getValue()).endTick <= this.tick) {
                    iterator.remove();
                }
            }
        }
    }

    public void set(SpellInterface spell, int duration) {
        this.entries.put(spell, new Entry(this.tick, this.tick + duration));
    }

    public void remove(SpellInterface spell) {
        this.entries.remove(spell);
    }

    private record Entry(int startTick, int endTick) {
    }
}
