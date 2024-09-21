package net.agent59.mixin.client;

import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.agent59.cardinal_component.ClientPlayerMagicComponent;
import net.agent59.cardinal_component.Components;
import net.agent59.cardinal_component.MagicComponent;
import net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * A hack to be able to use the {@link ClientPlayerMagicComponent}
 * on the client instead of the {@link ServerPlayerMagicComponent}, which is used by default.
 * <p>Does this by redirecting the registration call and replacing the ComponentFactory.
 * <p>If an integrated server is being created, both the default {@link ServerPlayerMagicComponent}
 * and the {@link ClientPlayerMagicComponent} are created, because a component is built for
 * the {@link ServerPlayerEntity} and the {@link net.minecraft.client.network.ClientPlayerEntity} each.
 * @see Components
 */
@Mixin(Components.class)
public class ComponentsMixin {

    @Redirect(
            method = "registerEntityComponentFactories",
            at = @At(value = "INVOKE", target = "registerForPlayers(Ldev/onyxstudios/cca/api/v3/component/ComponentKey;Ldev/onyxstudios/cca/api/v3/component/ComponentFactory;Ldev/onyxstudios/cca/api/v3/entity/RespawnCopyStrategy;)V"),
            remap = false
    )
    private <P extends ServerPlayerMagicComponent> void switchToClientPlayerMagicComponent(
            EntityComponentFactoryRegistry registry,
            ComponentKey<MagicComponent> key,
            ComponentFactory<PlayerEntity, P> factory,
            RespawnCopyStrategy<MagicComponent> respawnStrategy
    ) {
        registry.registerForPlayers(key, (player -> {
            if (player instanceof ServerPlayerEntity) return factory.createComponent(player);
            return new ClientPlayerMagicComponent(player);
        }), respawnStrategy);
    }
}
