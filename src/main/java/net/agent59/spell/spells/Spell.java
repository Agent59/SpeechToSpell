package net.agent59.spell.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.agent59.codecs.CodecUtil;
import net.agent59.resource.MergingJsonDataLoader;
import net.agent59.spell.SpellState;
import net.agent59.spell.SpellTypes;
import net.agent59.spell.component.SpellStateComponentMap;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Collections;
import java.util.Map;

/**
 * The base class for spells,
 * which can be cast by entities that have a {@link net.agent59.cardinal_component.MagicComponent}.
 *
 * <p>Like {@link Item} or {@link net.minecraft.block.Block},
 * this class handles the logics for a spell and does not hold any data apart from its initial configuration.<br>
 * The dynamic data of a spell (e.g. the cooldown), in regard to a specific entity, is held by a {@link SpellState}.
 * Every magical entity stores a state for each spell in its {@link net.agent59.cardinal_component.MagicComponent}.
 *
 * <p>Spells are loaded and contained by the {@link net.agent59.spell.SpellManager}.<br>
 * There might be multiple instances of the same Spell-Class in the SpellManager, but every loaded spell
 * will have a unique id and incantation.
 *
 * <p>A spell should not be confused with its {@link SpellTypes.SpellType},
 * which is used to determine which Spell-Class should be instantiated when loading the spell from json.
 * Unlike spells, SpellTypes are unique.<br>
 * For each Spell-Class there must be one registered SpellType, which the spell returns with {@link #getType()}.
 *
 * <p>View the {@link net.agent59.spell.spells} directory
 * to see how some implementations of Spell-Classes might look like.
 *
 * @see SpellTypes
 * @see net.agent59.spell.SpellManager
 * @see SpellState
 * @see net.agent59.cardinal_component.MagicComponent
 * @see net.agent59.cardinal_component.player_magic_comp.PlayerMagicComponent
 */
public abstract class Spell {
    /**
     * The basic configuration information every spell requires to properly function.
     */
    public final BaseConfiguration baseConfiguration;
    /**
     * Used so to not be needing to recreate {@link ItemIcon}s and thus ItemStacks when displaying icons of spells.
     * @see #getIcon()
     */
    public final Icon icon;

    public Spell(BaseConfiguration baseConfiguration) {
        this.baseConfiguration = baseConfiguration;
        this.icon = new ItemIcon(baseConfiguration.displayItem());
    }

    /**
     * Every spell needs to create a {@link SpellTypes.SpellType} entry in {@link SpellTypes}.
     * <p> The SpellType entry is used to determine which Spell-Class should be used when loading a spell from json.
     * <p>The id of the SpellType entry is not to be confused
     * with the id, which the loaded spell will later have in its BaseConfiguration.
     * @return The corresponding entry from {@link SpellTypes}.
     */
    public abstract SpellTypes.SpellType<? extends Spell> getType();

    /**
     * This check is not reached if there are already reasons for the spell not to be cast (e.g. it's cooling down).
     * @param state The state of the spell.
     * @return {@code true} when the spell can be cast.
     */
    public boolean canCast(SpellState state) {
        return true;
    }

    /**
     * Controls the main logic of the spell.
     *
     * <p> Is called as long as the spell is actively being cast.
     * The time for which the spell is cast is controlled in the spells state {@link SpellState#tickCasting()}.
     *
     * <p> If the casting should finish early,
     * <strong>{@link #endEarly(SpellState)}<u> is automatically called</u></strong>,
     * thus logic for cleaning up should be put in the endEarly method.
     *
     * @param state The state of the spell.
     * @return {@code true} when casting should finish early.
     */
    public abstract boolean tickCasting(SpellState state);

    /**
     * Is automatically called if the spell is stopped early during casting.
     *
     * <p>E.g. is called when an entity changes the spell it is currently casting.
     *
     * @param state The state of the spell.
     */
    public void endEarly(SpellState state) {}

    /**
     * Is called every tick the spell is cooling down.
     *
     * <p>Normally there is no need for spells to implement this method.
     *
     * <p>The actual logic for reducing the cooldown time is handled by {@link SpellState#cooldownTick()}.
     *
     * @param state The state of the spell.
     *
     * @return {@code true} when the cooldown should finish early.
     */
    public boolean tickCooldown(SpellState state) {
        return false;
    }

    /**
     * By overriding this method, spells can ensure that their state has the specified components at all times.
     *
     * <p><strong>Each pair of {@code ?} and {@code Object} must be of the same type, otherwise errors will arise!</strong>
     *
     * @return The {@link SpellStateComponentTypes.SpellStateComponentType}s that this spell requires<br>
     * (e.g. to work every tick) and their corresponding default values.
     */
    public Map<SpellStateComponentTypes.SpellStateComponentType<?>, Object> getRequiredSpellStateComponents() {
        return Collections.emptyMap();
    }

    /**
     * @param entity The entity the state should belong to.
     * @return The default state of the spell.
     */
    public SpellState getDefaultState(LivingEntity entity) {
        return new SpellState(entity, this,
                this.getBaseConf().defaultUnlockState(),
                new SpellStateComponentMap(this.getRequiredSpellStateComponents())
        );
    }

    /**
     * May be used as a shortcut to get the BaseConfiguration of the spell.
     * <p><i>Some more shortcut methods, that return specific configuration fields, are implemented below this method.</i>
     * @return The basic configuration of the spell.
     */
    public final BaseConfiguration getBaseConf() {
        return this.baseConfiguration;
    }

    /**
     * @return The id with which the spell is registered in the {@link net.agent59.spell.SpellManager}.
     */
    public final Identifier getId() {
        return this.getBaseConf().id();
    }

    /**
     * @return The word(s) that the speech recognition needs to recognize to cast the spell.
     */
    public final String getIncantation() {
        return this.getBaseConf().incantation();
    }

    /**
     * @return The name of the spell (Text formatting and translations can be used in the json files).
     */
    public Text getName() {
        return this.getBaseConf().name();
    }

    /**
     * @return The description of the spell (Text formatting and translations can be used in the json files).
     */
    public Text getDescription() {
        return this.getBaseConf().description();
    }

    /**
     * @return The item whose icon is displayed.
     * @see #getIcon()
     */
    public Item getDisplayItem() {
        return this.getBaseConf().displayItem();
    }

    /**
     * @return The spells {@link #icon}. Initially created with {@link #getDisplayItem()}.
     */
    public Icon getIcon() {
        return this.icon;
    }

    /**
     * @return In what way the spell is unlocked by default<br>(e.g. it's locked, unlocked, speechless casting is unlocked, etc.).
     */
    public SpellState.UnlockState getDefaultUnlockState() {
        return this.getBaseConf().defaultUnlockState();
    }

    /**
     * @return The ticks it takes to cool this spell down.
     */
    public int getCooldownTime() {
        return this.getBaseConf().cooldown();
    }

    /**
     * @return The time in ticks for which the spell is cast.
     */
    public int getCastingTime() {
        return this.getBaseConf().duration();
    }

    /**
     * Used for logging and debugging.
     * @return The string of the Spell-Object, with the spells base configuration appended to it.
     */
    @Override
    public String toString() {
        return super.toString() + this.getBaseConf();
    }

    /**
     * Contains the basic configuration options a spell needs to have and be able to provide.<br>
     * It is strongly advised that spells use the {@code BaseConfiguration.MAP_CODEC} for their own codec:
     * <pre> {@code
     *     public static final Codec<SomeSpell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
     *             BaseConfiguration.MAP_CODEC.forGetter(Spell::getBaseConf),
     *             // Add spell specific configuration options here, e.g.:
     *             Codec.INT.optionalFieldOf("example_int", 0).forGetter(SomeSpell::getExampleInt)
     *     ).apply(instance, TestSpell::new));
     * }</pre>
     *
     * @param id See {@link #getId()}
     * @param incantation See {@link #getIncantation()}
     * @param name See {@link #getName()}
     * @param description See {@link #getDescription()}
     * @param displayItem See {@link #getDisplayItem()}
     * @param defaultUnlockState See {@link #getDefaultUnlockState}
     * @param duration See {@link #getCastingTime()}
     * @param cooldown See {@link #getCooldownTime()}
     */
    public record BaseConfiguration(
            Identifier id,
            String incantation,
            Text name,
            Text description,
            Item displayItem,
            SpellState.UnlockState defaultUnlockState,
            int duration,
            int cooldown
    ) {
        public static final MapCodec<BaseConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf(MergingJsonDataLoader.ID_FIELD).forGetter(BaseConfiguration::id),
                Codec.STRING.fieldOf("incantation").forGetter(BaseConfiguration::incantation),
                CodecUtil.BETTER_TEXT.fieldOf("name").forGetter(BaseConfiguration::name),
                CodecUtil.BETTER_TEXT.optionalFieldOf("description", Text.empty()).forGetter(BaseConfiguration::description),
                Registries.ITEM.getCodec().optionalFieldOf("icon", Items.AIR).forGetter(BaseConfiguration::displayItem),
                SpellState.UnlockState.CODEC.fieldOf("default_unlock_state").forGetter(BaseConfiguration::defaultUnlockState),
                Codecs.NONNEGATIVE_INT.fieldOf("duration").forGetter(BaseConfiguration::duration),
                Codecs.NONNEGATIVE_INT.fieldOf("cooldown").forGetter(BaseConfiguration::cooldown)
        ).apply(instance, BaseConfiguration::new));
    }
}
