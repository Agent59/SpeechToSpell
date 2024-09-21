package net.agent59.spell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.agent59.Main;
import net.agent59.StSEventListeners;
import net.agent59.codecs.OptionalFieldCodec;
import net.agent59.registry.ModRegistries;
import net.agent59.resource.MergingJsonDataLoader;
import net.agent59.resource.StSReloadChangesEvents;
import net.agent59.spell.spells.Spell;
import net.agent59.spell_school.SpellSchool;
import net.agent59.spell_school.SpellSchoolManager;
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

import java.util.*;

/**
 * Holds the loaded {@link Spell}s.
 * <p>Is also a resource reload listener which loads the spells from datapacks.
 *
 * @see Spell
 * @see SpellSchoolManager
 * @see MergingJsonDataLoader
 * @see StSReloadChangesEvents
 */
public class SpellManager extends MergingJsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Set<Identifier> DEPENDENCIES = Set.of(new Identifier(Main.MOD_ID, SpellSchoolManager.PATH));
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String PATH = "spells";
    public static final Identifier SYNC_CHANNEL_NAME = Main.id("sync_spell_manager");
    /**
     * The ServerPlayerMagicComponent uses this to check whether a parsing error was an expected one.
     * @see net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent#onDataPacksReloaded()
     */
    public static final String NO_SUCH_REGISTERED_SPELL_ERROR = "No such registered spell: ";

    private static final Codec<Spell> DISPATCHER = ModRegistries.SPELL_TYPE.getCodec()
            .dispatch("spell_type", Spell::getType, SpellTypes.SpellType::codec);

    private static HashMap<Identifier, Spell> spells = new HashMap<>();
    private static HashMap<String, Spell> incantations = new HashMap<>();
    /**
     * {@code null} as long as no reload is in progress.
     */
    private static @Nullable HashMap<Identifier, Spell> unAppliedSpells;
    /**
     * {@code null} as long as no reload is in progress.
     */
    private static @Nullable HashMap<String, Spell> unAppliedIncantations;

    public SpellManager() {
        super(GSON, PATH);
    }

    public static @Unmodifiable Map<Identifier, Spell> getSpells() {
        return spells;
    }

    public static @Unmodifiable Spell[] getSpellsArray() {
        return getSpells().values().toArray(new Spell[0]);
    }

    public static @Nullable Spell getSpell(Identifier id) {
        return spells.get(id);
    }

    public static @Nullable Spell getSpell(String incantation) {
        return incantations.get(incantation);
    }

    public static Codec<Spell> getCodec() {
        return Identifier.CODEC.flatXmap(
                id -> Optional.ofNullable(getSpell(id))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> NO_SUCH_REGISTERED_SPELL_ERROR + id)),
                spell -> spell.equals(getSpell(spell.getId())) ?
                        DataResult.success(spell.getId()) :
                        DataResult.error(() -> "The spell " + spell + " has not been loaded into the SpellManager")
        );
    }

    public static Codec<Optional<Spell>> getOptionalCodec() {
        return new OptionalFieldCodec<>(Main.id("spell").toString(), getCodec(), false).codec();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Spell> Codec<T> getClassSpecificCodec(Class<T> spellClass) {
        return getCodec().comapFlatMap(
                spell -> spellClass.isInstance(spell) ?
                        DataResult.success((T) spell) :
                        DataResult.error(() -> "Spell " + spell + " is not of class " + spellClass),
                spell -> spell
        );
    }

    /**
     * Creates {@link Spell}s from the {@link JsonObject}s
     * that were merged from datapacks by the {@link MergingJsonDataLoader}.
     * <p>Codecs are used to parse the JsonObjects into spells.
     * The spells are then added to the static {@link #unAppliedSpells} and {@link #unAppliedIncantations} map.
     * This means the changes are not fully applied yet.
     * <p>The changes are actually fully applied in {@link #applyChanges()}.
     * @param prepared  Keys: resources ids; Values: spells data
     * @param manager the resource manager
     * @param profiler the apply profiler
     * @see StSReloadChangesEvents
     */
    @Override
    protected void apply(Map<Identifier, JsonObject> prepared, ResourceManager manager, Profiler profiler) {
        Main.LOGGER.info("Attempting to load the spells {}.", prepared.keySet());
        unAppliedSpells = new HashMap<>();
        unAppliedIncantations = new HashMap<>();

        prepared.forEach((spellId, jsonObj) -> {
            // This is required because the codec needs an id field.
            MergingJsonDataLoader.addIdField(jsonObj, spellId);

            DISPATCHER.parse(JsonOps.INSTANCE, jsonObj)
                    .resultOrPartial((errMsg) -> Main.LOGGER.warn("""
                            Could not parse the Codec for the spell {} when attempting to load it from json {}\
                            due to the following error:
                            {}
                            The json entry will be skipped.""", spellId, jsonObj, errMsg)
                    ).ifPresent(spell -> {
                        String incantation = spell.getIncantation();
                        if (unAppliedIncantations.containsKey(incantation)) {
                            Main.LOGGER.warn("Spell {} and spell {} have the same incantation {}. " +
                                            "Not loading the spell {}.", spell,
                                    unAppliedIncantations.get(incantation), incantation, spell
                            );
                        } else {
                            unAppliedSpells.put(spellId, spell);
                            unAppliedIncantations.put(incantation, spell);
                        }
                    });
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
        assert unAppliedSpells != null;
        assert unAppliedIncantations != null;
        spells = unAppliedSpells;
        incantations = unAppliedIncantations;
        unAppliedSpells = null;
        unAppliedIncantations = null;
        Main.LOGGER.info("Successfully loaded the spells {}.", spells.keySet());
    }

    /**
     * Syncs the SpellManager to the given player.
     */
    public static void syncToClient(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCollection(spells.values(), (buf2, spell) -> buf2.encodeAsJson(DISPATCHER, spell));
        ServerPlayNetworking.send(player, SYNC_CHANNEL_NAME, buf);
    }

    /**
     * The receiving end of {@link #syncToClient(ServerPlayerEntity)}.
     */
    @Environment(EnvType.CLIENT)
    public static void syncReceiver(PacketByteBuf buf) {
        buf.forEachInCollection(buf2 -> {
            Spell spell = buf2.decodeAsJson(DISPATCHER);
            spells.put(spell.getId(), spell);
            incantations.put(spell.getIncantation(), spell);
        });
    }

    @Override
    public Identifier getFabricId() {
        return Main.id(PATH);
    }

    /**
     * Ensures that the {@link SpellSchool}s are loaded before the {@link Spell}s are loaded.
     * @see SpellSchoolManager
     */
    @Override
    public Collection<Identifier> getFabricDependencies() {
        return DEPENDENCIES;
    }
}
