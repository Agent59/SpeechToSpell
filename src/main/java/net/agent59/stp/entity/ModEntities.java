package net.agent59.stp.entity;

import net.agent59.stp.Main;
import net.agent59.stp.entity.custom.RayEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModEntities {

    public static final EntityType<RayEntity> STUPEFY_RAY = registerEntity("stupefy_ray",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, RayEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).build());

    public static final EntityType<RayEntity> EXPELLIARMUS_RAY = registerEntity("expelliarmus_ray",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, RayEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).build());

    public static final EntityType<RayEntity> PETRIFICUS_TOTALUS_RAY = registerEntity("petrificus_totalus_ray",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, RayEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).build());

    public static final EntityType<RayEntity> PROTEGO_WALL = registerEntity("protego_wall",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, RayEntity::new).dimensions(EntityDimensions.fixed(1.5F, 2.25F)).build());

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Main.MOD_ID, name), entityType);
    }

    public static void registerModEntities() {
        Main.LOGGER.info("Registering Mod Entities for " + Main.MOD_ID);
    }
}
