package io.github.kawaiicakes.vsutil.tournament.services;

import io.github.kawaiicakes.vsutil.tournament.registry.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface DeferredRegisterBackend {
    <T> DeferredRegister<T> makeDeferredRegister(String id, ResourceKey<Registry<T>> registry);
}
