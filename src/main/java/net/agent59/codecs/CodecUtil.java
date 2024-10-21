package net.agent59.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public class CodecUtil {

    /**
     * Like the default codec, but ensures that the first style that was created does not affect following parts,
     * that don't  have their own style.
     * <p>E.g. when displaying {@code [{"text": "colored text", color: "green"}, "non styled text"]}
     * {@code "none styled text"} is displayed in green,
     * although it is not styled and should use minecraft's default color.
     * <p>This is fixed by inserting an empty text at the beginning, which this codec does.
     */
    public static final Codec<Text> BETTER_TEXT = Codecs.TEXT.xmap(
            text -> {
                MutableText result = Text.empty().append(text.copyContentOnly().setStyle(text.getStyle()));
                for (Text part : text.getSiblings()) result.append(part);
                return result;
            },
            text -> text
    );

    public static <E extends Enum<E>> Codec<E> getEnumCodec(Class<E> enumClass) {
        return Codec.STRING.comapFlatMap(
                str -> {
                    try {
                        return DataResult.success(Enum.valueOf(enumClass, str.toUpperCase()));
                    } catch (Exception e) {
                        return DataResult.error(() -> "Cannot convert " + str + " to type of enum class " + enumClass);
                    }
                },
                Enum::name
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Item> Codec<T> getItemClassSpecificCodec(Class<T> itemClass) {
        return Registries.ITEM.getCodec().comapFlatMap(
                item -> itemClass.isInstance(item) ?
                        DataResult.success((T) item) :
                        DataResult.error(() -> "Item " + item + " is not of class " + itemClass),
                item -> item
        );
    }
}
