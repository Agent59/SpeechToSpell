package net.agent59.stp.mixin;

import net.agent59.stp.spell.PlayerEntityInterface;
import net.agent59.stp.spell.SpellCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// injects into the tick method and updates the SpellCooldownManager
// this is similar to minecrafts ItemCooldownManager

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityInterface {
    public final SpellCooldownManager spellCooldownManager = new SpellCooldownManager();

    @Inject(method = "tick", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {
        this.spellCooldownManager.update();
        System.out.println("\n HERE \n");
    }

    public SpellCooldownManager getSpellCooldownManager() {
        return this.spellCooldownManager;
    }
}
