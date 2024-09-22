package net.agent59.mixin;

import net.agent59.resource.StSReloadChangesEvents;
import net.agent59.spell.SpellManager;
import net.agent59.spell_school.SpellSchoolManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    /**
     * Called after {@code getPlayerManager().saveAllPlayerData()}
     * in {@link MinecraftServer#reloadResources(Collection)}.
     *
     * <p>After the PlayerData has been saved during the reload,
     * the reload-changes of the {@link SpellManager} and the {@link SpellSchoolManager} can be applied.
     */
    @Inject(method = "onDataPacksReloaded", at = @At("HEAD"))
    private void afterPlayerDataSave(CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        StSReloadChangesEvents.START_APPLY_STS_RELOAD_CHANGES.invoker().startApplyStSReloadChanges(server);

        SpellSchoolManager.applyChanges();
        SpellManager.applyChanges();

        StSReloadChangesEvents.END_APPLY_STS_RELOAD_CHANGES.invoker().endApplyStSReloadChanges(server);
    }
}
