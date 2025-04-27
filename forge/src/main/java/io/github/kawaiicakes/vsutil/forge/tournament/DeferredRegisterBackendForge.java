package io.github.kawaiicakes.vsutil.forge.tournament;

import io.github.kawaiicakes.vsutil.tournament.registry.DeferredRegister;
import io.github.kawaiicakes.vsutil.tournament.services.DeferredRegisterBackend;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;


public class DeferredRegisterBackendForge implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl<>(id, registry);
    }
}
