package com.startraveler.murmur.registry;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public class CodecRegistry<T> {

    private final HashBiMap<ResourceLocation, MapCodec<? extends T>> map = HashBiMap.create();

    public <S extends T> CodecRegistryEntry<S> register(ResourceLocation location, MapCodec<S> type) {
        if (type != null) {
            this.map.put(location, type);
        } else {
            throw new IllegalArgumentException("Can only register one codec for " + location + "!");
        }

        return new CodecRegistryEntry<>(location, type);
    }

    public MapCodec<? extends T> byKey(ResourceLocation location) {
        return this.map.get(location);
    }

    public ResourceLocation byValue(MapCodec<?> type) {
        return this.map.inverse().get(type);
    }

    public Codec<MapCodec<? extends T>> forDispatch() {
        return ResourceLocation.CODEC.xmap(
                this::byKey,
                this::byValue
        );
    }

    public record CodecRegistryEntry<S>(ResourceLocation location, MapCodec<S> codec) {
    }

}
