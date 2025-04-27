package io.github.kawaiicakes.vsutil.fabric;

import io.github.kawaiicakes.vsutil.Commands;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.api.DisabledCollisionData;
import io.github.kawaiicakes.vsutil.item.NoCollisionWand;
import io.github.kawaiicakes.vsutil.tournament.TickScheduler;
import io.github.kawaiicakes.vsutil.tournament.TournamentBlocks;
import io.github.kawaiicakes.vsutil.tournament.TournamentItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;
import static net.minecraft.commands.Commands.literal;

public class VSUtilFabric implements ModInitializer {
    public static final Item COLLISION_WAND = Registry.register(
            Registry.ITEM,
            new ResourceLocation(MOD_ID + ":collision_wand"),
            new NoCollisionWand()
    );

    @Override
    public void onInitialize() {
        // force VS2 to load before this
        new ValkyrienSkiesModFabric().onInitialize();
        VSUtil.init();

        CommandRegistrationCallback.EVENT.register((a,b,c) -> a.register(Commands.registerCommands(literal(MOD_ID))));

        ServerWorldEvents.LOAD.register((server, level) -> {
            CollisionPairData.load(level);
            DisabledCollisionData.load(level);
        });

        TournamentItems.INSTANCE.TAB = FabricItemGroupBuilder
                .create(new ResourceLocation(VSUtil.MOD_ID, "main_tab"))
                .icon(() -> new ItemStack(TournamentBlocks.INSTANCE.PROP_SMALL.get()))
                .build();

        ServerTickEvents.END_SERVER_TICK.register(TickScheduler.INSTANCE::tickServer);

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> VSUtilImpl.SERVER = server.overworld().getServer());
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> VSUtilImpl.SERVER = null);
    }
}
