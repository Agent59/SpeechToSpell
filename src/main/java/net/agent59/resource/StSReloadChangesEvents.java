package net.agent59.resource;

import net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent;
import net.agent59.spell.SpellManager;
import net.agent59.spell_school.SpellSchoolManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The events that are called before and after the changes,
 * that are caused by loaded by the {@link SpellManager} and {@link SpellSchoolManager}, are applied.
 * <p>The process for reloading is as follows (all steps are done on the server):
 * <ol>
 *      <li>The resources (json files) are read and prepared by the {@link MergingJsonDataLoader}.</li>
 *      <li>The spells and schools are loaded by the {@link SpellManager} and {@link SpellSchoolManager},
 *      but the changes are not yet applied (although the method names {@code apply(...)} might suggest that).</li>
 *      <li>The player-data (of the magic components) is saved.</li>
 *      <li>The {@link #START_APPLY_STS_RELOAD_CHANGES} event is triggered.</li>
 *      <li>The reload changes are applied ({@link net.agent59.mixin.PlayerManagerMixin#afterPlayerDataSave(CallbackInfo)})</li>
 *      <li>The {@link #END_APPLY_STS_RELOAD_CHANGES} event is triggered.</li>
 *      <li>{@link ServerPlayerMagicComponent#onDataPacksReloaded()} is called for each player
 *      and the data is synced to the client.</li>
 * </ol>
 * @see net.agent59.StSEventListeners
 */
public class StSReloadChangesEvents {

    public static final Event<StartApplyStSReloadChanges> START_APPLY_STS_RELOAD_CHANGES =
            EventFactory.createArrayBacked(StartApplyStSReloadChanges.class, callbacks -> (server) -> {
                for (StartApplyStSReloadChanges callback : callbacks) {
                    callback.startApplyStSReloadChanges(server);
                }
            });

    public static final Event<EndApplyStSReloadChanges> END_APPLY_STS_RELOAD_CHANGES =
            EventFactory.createArrayBacked(EndApplyStSReloadChanges.class, callbacks -> (server) -> {
                for (EndApplyStSReloadChanges callback : callbacks) {
                    callback.endApplyStSReloadChanges(server);
                }
            });

    @FunctionalInterface
    public interface StartApplyStSReloadChanges {
        void startApplyStSReloadChanges(MinecraftServer server);
    }

    @FunctionalInterface
    public interface EndApplyStSReloadChanges {
        void endApplyStSReloadChanges(MinecraftServer server);
    }
}
