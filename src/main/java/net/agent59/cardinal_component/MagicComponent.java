package net.agent59.cardinal_component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.agent59.spell.SpellState;
import net.agent59.spell.spells.Spell;
import net.agent59.spell_school.SpellSchool;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * An interface for components that give entities magical abilities.
 * @see Components
 * @see dev.onyxstudios.cca.api.v3.component.Component
 * @see net.agent59.cardinal_component.player_magic_comp.PlayerMagicComponent
 */
public interface MagicComponent extends AutoSyncedComponent, ServerTickingComponent {
    SpellSchool getSpellSchool();
    @Nullable Spell getActiveSpell();
    Set<Spell> getSpellsCoolingDown();
    SpellState getSpellsState(Spell spell);
    boolean canCast(Spell spell, boolean speechless);
    void castSpell(Spell spell, boolean speechless);
}
