package io.github.kawaiicakes.vsutil.tournament;

import net.minecraft.server.MinecraftServer;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TickScheduler {
    public static TickScheduler INSTANCE = new TickScheduler();

    private final ConcurrentHashMap.KeySetView<Ticking, Boolean> serverTickPerm = ConcurrentHashMap.newKeySet();

    /**
     * Adds a permanent task to be run every tick on the server thread.
     */
    public Ticking serverTickPerm(Consumer<MinecraftServer> f) {
        Ticking t = new Ticking(f, true);
        serverTickPerm.add(t);
        return t;
    }

    /**
     * Should be called by a mixin in the server tick loop.
     */
    public void tickServer(MinecraftServer server) {
        LinkedHashSet<Ticking> toRemove = new LinkedHashSet<>();

        serverTickPerm.forEach(t -> {
            if (!t.active) {
                toRemove.add(t);
                return;
            }

            t.f.accept(server);
        });

        serverTickPerm.removeAll(toRemove);
    }

    private TickScheduler() {}

    /**
     * A task to be run on the server thread.
     * @param f The function to be run.
     * @param active    If false, will be removed after the next tick.
     */
    public record Ticking(
            Consumer<MinecraftServer> f,
            boolean active
    ) {}
}
