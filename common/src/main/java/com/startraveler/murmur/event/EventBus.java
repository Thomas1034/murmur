package com.startraveler.murmur.event;

import com.startraveler.murmur.registry.CodecRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;

public class EventBus<S> {

    protected final Map<ResourceLocation, List<Consumer<S>>> consumers;

    public EventBus() {
        this.consumers = new HashMap<>();
    }

    public void register(CodecRegistry.CodecRegistryEntry<? extends S> type, Consumer<S> handler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(handler);
        List<Consumer<S>> existing = this.consumers.computeIfAbsent(type.location(), k -> new ArrayList<>());
        existing.add(handler);
    }

    public void fire(CodecRegistry.CodecRegistryEntry<? extends S> type, S event) {
        List<Consumer<S>> existing = this.consumers.get(type.location());
        if (null != existing) {
            for (Consumer<S> handler : existing) {
                handler.accept(event);
            }
        }
    }
}
