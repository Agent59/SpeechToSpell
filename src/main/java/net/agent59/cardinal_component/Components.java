package net.agent59.cardinal_component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.agent59.Main;
import net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent;

/**
 * <strong>The {@link ServerPlayerMagicComponent} is replaced on the client with the {@code ClientPlayerMagicComponent}.
 * <p>This is done via mixins in the {@code ComponentsMixin} file in the client source.</strong><br>
 */
public final class Components implements EntityComponentInitializer {

    public static final ComponentKey<MagicComponent> MAGICIAN =
            ComponentRegistry.getOrCreate(Main.id( "magic"), MagicComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MAGICIAN, ServerPlayerMagicComponent::new, RespawnCopyStrategy.CHARACTER);
    }
}
