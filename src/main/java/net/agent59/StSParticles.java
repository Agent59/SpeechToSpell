package net.agent59;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class StSParticles {

    public static final DefaultParticleType EMPTY_PARTICLE = FabricParticleTypes.simple();

    public static void registerModParticles() {
        Main.LOGGER.info("Registering Mod Particles for " + Main.MOD_NAME);

        Registry.register(Registries.PARTICLE_TYPE, Main.id("empty"), EMPTY_PARTICLE);
    }

}
