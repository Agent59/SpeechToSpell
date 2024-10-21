package net.agent59.spell_school;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agent59.codecs.CodecUtil;
import net.agent59.item.custom.WandItem;
import net.agent59.resource.MergingJsonDataLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: As of right now, this provides no actual functionality
/**
 * A school an entity can be of.
 *
 * @param id The id with which the school is registered in the {@link SpellSchoolManager}.
 * @param name The name of the school.
 * @param description The description of the school.
 * @param compatibleWands The wands magicians may use if they are of the school.
 */
public record SpellSchool(
        Identifier id,
        Text name,
        Text description,
        Item displayItem,
        Set<WandItem> compatibleWands
) {

    public static final Codec<SpellSchool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf(MergingJsonDataLoader.ID_FIELD).forGetter(SpellSchool::id),
            CodecUtil.BETTER_TEXT.fieldOf("name").forGetter(SpellSchool::name),
            CodecUtil.BETTER_TEXT.optionalFieldOf("description", Text.empty()).forGetter(SpellSchool::description),
            Registries.ITEM.getCodec().optionalFieldOf("icon", Items.AIR).forGetter(SpellSchool::displayItem),
            CodecUtil.getItemClassSpecificCodec(WandItem.class).listOf().optionalFieldOf("compatible_wands", new ArrayList<>()).forGetter(SpellSchool::compatibleWandsList)
    ).apply(instance, SpellSchool::new));

    public SpellSchool(Identifier id, Text name, Text description, Item displayItem, List<WandItem> compatibleWands) {
        this(id, name, description, displayItem, new HashSet<>(compatibleWands));
    }

    public List<WandItem> compatibleWandsList() {
        return new ArrayList<>(compatibleWands);
    }
}
