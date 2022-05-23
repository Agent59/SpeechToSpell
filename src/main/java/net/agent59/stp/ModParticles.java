package net.agent59.stp;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles {

    public static final DefaultParticleType EMPTY_PARTICLE = FabricParticleTypes.simple();

    public static void registerModParticles() {
        Main.LOGGER.info("Registering Mod Particles for " + Main.MOD_ID);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Main.MOD_ID, "empty"), EMPTY_PARTICLE);
    }

}
