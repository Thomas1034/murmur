package com.startraveler.murmur.event;

import com.startraveler.murmur.registry.CodecRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventBus<S> {

    protected final Map<ResourceLocation, List<BiConsumer<S, Object[]>>> consumers;

    public EventBus() {
        this.consumers = new HashMap<>();
    }

    public void register(CodecRegistry.CodecRegistryEntry<? extends S> type, BiConsumer<S, Object[]> handler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(handler);
        List<BiConsumer<S, Object[]>> existing = this.consumers.computeIfAbsent(type.location(), k -> new ArrayList<>());
        existing.add(handler);
    }

    public void fire(CodecRegistry.CodecRegistryEntry<? extends S> type, S event, Object... payload) {
        List<BiConsumer<S, Object[]>> existing = this.consumers.get(type.location());
        if (null != existing) {
            for (BiConsumer<S, Object[]> handler : existing) {
                handler.accept(event, payload);
            }
        }
    }
}
