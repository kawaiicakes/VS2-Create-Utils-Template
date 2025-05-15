package io.github.kawaiicakes.vsutil.fabric;

import io.github.kawaiicakes.vsutil.Commands;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.api.DisabledCollisionData;
import io.github.kawaiicakes.vsutil.network.fabric.VSUtilPacketsImpl;
import io.github.kawaiicakes.vsutil.tournament.TickScheduler;
import io.github.kawaiicakes.vsutil.tournament.TournamentItems;
import io.github.kawaiicakes.vsutil.tournament.registry.CreativeTabs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;
import static net.minecraft.commands.Commands.literal;

public class VSUtilFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before this
        new ValkyrienSkiesModFabric().onInitialize();
        VSUtil.init();

        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                TournamentItems.TAB,
                CreativeTabs.create()
        );

        CommandRegistrationCallback.EVENT.register((a,b,c) -> a.register(Commands.registerCommands(literal(MOD_ID))));

        ServerWorldEvents.LOAD.register((server, level) -> {
            CollisionPairData.load(level);
            DisabledCollisionData.load(level);
        });

        VSUtilPacketsImpl.register();

        ServerTickEvents.END_SERVER_TICK.register(TickScheduler.INSTANCE::tickServer);

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> VSUtilImpl.SERVER = server.overworld().getServer());
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> VSUtilImpl.SERVER = null);
    }
}
