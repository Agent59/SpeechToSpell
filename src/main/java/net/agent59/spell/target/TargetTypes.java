package net.agent59.spell.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.agent59.StSMain;
import net.agent59.registry.StSRegistries;
import net.agent59.spell.target.targets.Target;
import net.agent59.spell.target.targets.block.AnyBlockTarget;
import net.agent59.spell.target.targets.block.NoAirBlockTarget;
import net.agent59.spell.target.targets.entity.*;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * A registry of {@link Target}s.
 * <p>Spells may use the {@link #CODEC} to make their target configurable.
 * <p>If only entities or blocks can be configured as the target,
 * {@link #ENTITY_CODEC} or {@link #BLOCK_CODEC} may be used respectively.
 * <p><b>Not to be confused with {@link TargetAreaTypes}.</b>
 * @see <a href="https://docs.fabricmc.net/1.20.4/develop/codecs#registry-dispatch">Fabric-Wiki Registry Dispatch</a>
 */
public class TargetTypes {
    public static final Codec<Target> CODEC = StSRegistries.TARGET_TYPE.getCodec()
            .dispatch("type", Target::getType, TargetType::codec);

    public static final Codec<Target.EntityTarget> ENTITY_CODEC = CODEC.comapFlatMap(
            t -> t instanceof Target.EntityTarget ? DataResult.success((Target.EntityTarget) t) :
                    DataResult.error(() -> "Cannot convert the target to an EntityTarget"),
            t -> t
    );

    public static final Codec<Target.BlockTarget> BLOCK_CODEC = CODEC.comapFlatMap(
            t -> t instanceof Target.BlockTarget ? DataResult.success((Target.BlockTarget) t) :
                    DataResult.error(() -> "Cannot convert the target to a BlockTarget"),
            t -> t
    );

    public static final TargetType<AnyBlockTarget> BLOCK = register("block", AnyBlockTarget.CODEC);
    public static final TargetType<NoAirBlockTarget> NO_AIR_BLOCK = register("no_air_block", NoAirBlockTarget.CODEC);

    public static final TargetType<SimpleEntityTarget> ENTITY = register("entity", SimpleEntityTarget.CODEC);
    public static final TargetType<SelfTarget> SELF = register("self", SelfTarget.CODEC);
    public static final TargetType<LivingEntityTarget> LIVING_ENTITY = register("living_entity", LivingEntityTarget.CODEC);
    public static final TargetType<HostileEntityTarget> HOSTILE_ENTITY = register("hostile_entity", HostileEntityTarget.CODEC);
    public static final TargetType<PlayerTarget> PLAYER = register("player", PlayerTarget.CODEC);
    public static final TargetType<HostileOrPlayerTarget> HOSTILE_OR_PLAYER = register("hostile_or_player", HostileOrPlayerTarget.CODEC);

    /**
     * This method may only be used to register the target types, the SpeechToSpell mod implements directly.
     */
    private static <T extends Target> TargetType<T> register(String name, Codec<T> targetTypeCodec) {
        return register(StSMain.id(name), targetTypeCodec);
    }

    /**
     * Registers a new {@link Target}.
     * <p>This method may be used by other mods that want to add targets.<br>
     * The targets added by other mods may also be used for the built-in spells.
     * @param identifier The id of the target.
     * @param targetTypeCodec The codec determines how a target can be configured from json.
     * @param <T> The class of the target.
     */
    public static <T extends Target> TargetType<T> register(Identifier identifier, Codec<T> targetTypeCodec) {
        return Registry.register(StSRegistries.TARGET_TYPE, identifier, new TargetType<>(targetTypeCodec));
    }

    public record TargetType<T extends Target>(Codec<T> codec) {}
}
