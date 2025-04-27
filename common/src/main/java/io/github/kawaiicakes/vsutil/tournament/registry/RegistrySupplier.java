package io.github.kawaiicakes.vsutil.tournament.registry;

public interface RegistrySupplier<T> {
    String getName();
    T get();
}
