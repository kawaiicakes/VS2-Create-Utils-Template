package io.github.kawaiicakes.vsutil.tournament.registry;

import io.github.kawaiicakes.vsutil.tournament.services.DeferredRegisterBackend;
import kotlin.jvm.functions.Function0;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.ServiceLoader;

public interface DeferredRegister<T> extends Iterable<RegistrySupplier<T>> {
    DeferredRegisterBackend BACKEND = load();
    private static DeferredRegisterBackend load() {
        return ServiceLoader.load(DeferredRegisterBackend.class)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for DeferredRegisterBackend"));
    }

    <I extends T> RegistrySupplier<I> register(String name, Function0<? extends I> builder);

    void applyAll();

    static <T> DeferredRegister<T> create(String id, ResourceKey<Registry<T>> registry) {
        return BACKEND.makeDeferredRegister(id, registry);
    }
}
