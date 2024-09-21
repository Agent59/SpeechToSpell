package net.agent59.spell_school;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.agent59.Main;
import net.agent59.StSEventListeners;
import net.agent59.codecs.OptionalFieldCodec;
import net.agent59.resource.MergingJsonDataLoader;
import net.agent59.resource.StSReloadChangesEvents;
import net.agent59.spell.SpellManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
/**
 * Holds the loaded {@link SpellSchool}s.
 * <p>Is also a resource reload listener which loads the schools from datapacks.
 *
 * @see SpellSchool
 * @see SpellManager
 * @see MergingJsonDataLoader
 * @see StSReloadChangesEvents
 */
public class SpellSchoolManager extends MergingJsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String PATH = "schools";
    public static final Identifier SYNC_CHANNEL_NAME = Main.id("sync_spell_school_manager");
    /**
     * The ServerPlayerMagicComponent uses this to check whether a parsing error was an expected one.
     * @see net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent#onDataPacksReloaded()
     */
    public static String NO_SUCH_REGISTERED_SPELL_SCHOOL_ERROR = "No such registered spellSchool: ";

    private static HashMap<Identifier, SpellSchool> spellSchools = new HashMap<>();
    /**
     * {@code null} as long as no reload is in progress.
     */
    private static @Nullable HashMap<Identifier, SpellSchool> unAppliedSpellSchools;

    public SpellSchoolManager() {
        super(GSON, PATH);
    }

    public static @Unmodifiable Map<Identifier, SpellSchool> getSchools() {
        return spellSchools;
    }

    public static @Unmodifiable SpellSchool[] getSchoolsArray() {
        return getSchools().values().toArray(new SpellSchool[0]);
    }

    public static @Nullable SpellSchool getSchool(Identifier id) {
        return spellSchools.get(id);
    }

    public static @Nullable SpellSchool getRandomSchool() {
        if (spellSchools.isEmpty()) return null;
        Random generator = new Random();
        return getSchoolsArray()[generator.nextInt(spellSchools.size())];
    }

    public static Codec<SpellSchool> getCodec() {
        return Identifier.CODEC.flatXmap(
                id -> Optional.ofNullable(getSchool(id))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> NO_SUCH_REGISTERED_SPELL_SCHOOL_ERROR + id)),
                school -> school.equals(getSchool(school.id())) ?
                        DataResult.success(school.id()) :
                        DataResult.error(() -> "The school " + school + " has not been loaded into the SpellSchoolsManager.")
        );
    }

    public static Codec<Optional<SpellSchool>> getOptionalCodec() {
        return new OptionalFieldCodec<>(Main.id("spell_school").toString(), getCodec(), false).codec();
    }

    /**
     * Creates {@link SpellSchool}s from the {@link JsonObject}s
     * that were merged from datapacks by the {@link MergingJsonDataLoader}.
     * <p>Codecs are used to parse the JsonObjects into SpellSchools.
     * The SpellSchools are then added to the static {@link #unAppliedSpellSchools} map.
     * This means the changes are not fully applied yet.
     * <p>The changes are actually fully applied in {@link #applyChanges()}.
     * @param prepared  Keys: resources ids; Values: SpellSchools data
     * @param manager the resource manager
     * @param profiler the apply profiler
     * @see StSReloadChangesEvents
     */
    @Override
    protected void apply(Map<Identifier, JsonObject> prepared, ResourceManager manager, Profiler profiler) {
        Main.LOGGER.info("Attempting to load the spellSchools {}.", prepared.keySet());
        unAppliedSpellSchools = new HashMap<>();

        prepared.forEach((schoolId, jsonObj) -> {
            // This is required because the codec needs an id field.
            MergingJsonDataLoader.addIdField(jsonObj, schoolId);

            SpellSchool.CODEC.parse(JsonOps.INSTANCE, jsonObj)
                    .resultOrPartial((errMsg) -> Main.LOGGER.warn("""
                            Could not parse the Codec for the spellSchool {} when attempting to load it from json \
                            due to the following error:
                            {}
                            The json entry will be skipped.""", schoolId, errMsg)
                    ).ifPresent(spellSchool -> unAppliedSpellSchools.put(schoolId, spellSchool));
        });
        // If the server / world is loading (not reloading), the player-data is not saved first,
        // because there was nothing loaded to begin with.
        if (!StSEventListeners.isReloadInProgress()) applyChanges();
    }

    /**
     * During a reload the changes can't be applied directly, because the player-data needs to be saved first.<br>
     * This method is then called to actually apply the changes from {@link #apply(Map, ResourceManager, Profiler)}.
     * @see net.agent59.mixin.PlayerManagerMixin
     */
    public static void applyChanges() {
        assert unAppliedSpellSchools != null;
        spellSchools = unAppliedSpellSchools;
        unAppliedSpellSchools = null;
        Main.LOGGER.info("Successfully loaded the spellSchools {}.", spellSchools.keySet());
    }

    /**
     * Syncs the SpellSchoolManager to the given player.
     */
    public static void syncToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCollection(spellSchools.values(), (buf2, school) -> buf2.encodeAsJson(SpellSchool.CODEC, school));
        ServerPlayNetworking.send(player, SYNC_CHANNEL_NAME, buf);
    }

    /**
     * The receiving end of {@link #syncToClient(ServerPlayerEntity)}.
     */
    @Environment(EnvType.CLIENT)
    public static void syncReceiver(PacketByteBuf buf) {
        buf.forEachInCollection(buf2 -> {
            SpellSchool school = buf2.decodeAsJson(SpellSchool.CODEC);
            spellSchools.put(school.id(), school);
        });
    }

    @Override
    public Identifier getFabricId() {
        return Main.id(PATH);
    }
}
