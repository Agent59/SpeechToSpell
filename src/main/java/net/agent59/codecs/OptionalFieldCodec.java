// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// Ported by agent59.
// If this mod is updated to future minecraft versions, this port will become unnecessary and should be deleted.
// The portions of code that are edited out are the original lines,
// whereas the line below each of such comment is a modification.
// Before all entirely new portions a comment containing the word "additional" can be found.

//package com.mojang.serialization.codecs;
package net.agent59.codecs;

import com.mojang.serialization.*;
import net.agent59.Main;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

// /** Optimization of `Codec.either(someCodec.field(name), Codec.EMPTY)` */

// additional javadoc
/**
 * A port of the OptionalFieldCodec from the DataFixerUpper library versions.
 *
 * @see <a href="https://github.com/Mojang/DataFixerUpper/blob/master/src/main/java/com/mojang/serialization/codecs/OptionalFieldCodec.java">Mojang DataFixerUpper OptionalFieldCodec</a>
 */
public class OptionalFieldCodec<A> extends MapCodec<Optional<A>> {
    private final String name;
    private final Codec<A> elementCodec;
    private final boolean lenient;

    public OptionalFieldCodec(final String name, final Codec<A> elementCodec, final boolean lenient) {
        this.name = name;
        this.elementCodec = elementCodec;
        this.lenient = lenient;
    }

    @Override
    public <T> DataResult<Optional<A>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) {
            return DataResult.success(Optional.empty());
        }
        final DataResult<A> parsed = elementCodec.parse(ops, value);
        //if (parsed.isError() && lenient) {
        if (parsed.error().isEmpty() && lenient) {
            return DataResult.success(Optional.empty());
        }
        //return parsed.map(Optional::of).setPartial(parsed.resultOrPartial());
        return parsed.map(Optional::of).setPartial(parsed.resultOrPartial(Main.LOGGER::error));
    }

    @Override
    public <T> RecordBuilder<T> encode(final Optional<A> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        if (input.isPresent()) {
            return prefix.add(name, elementCodec.encodeStart(ops, input.get()));
        }
        return prefix;
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OptionalFieldCodec<?> that = (OptionalFieldCodec<?>) o;
        return Objects.equals(name, that.name) && Objects.equals(elementCodec, that.elementCodec) && lenient == that.lenient;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, elementCodec, lenient);
    }

    @Override
    public String toString() {
        return "OptionalFieldCodec[" + name + ": " + elementCodec + ']';
    }
}
