package net.agent59.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class CodecUtil {

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
