package io.github.kawaiicakes.vsutil.fabric;

import io.github.kawaiicakes.vsutil.Commands;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.item.NoCollisionWand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;
import static net.minecraft.commands.Commands.literal;

public class VSUtilFabric implements ModInitializer {
    public static final Item COLLISION_WAND = Registry.register(
            BuiltInRegistries.ITEM,
            new ResourceLocation(MOD_ID + ":collision_wand"),
            new NoCollisionWand()
    );

    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();
        VSUtil.init();

        CommandRegistrationCallback.EVENT.register((a,b,c) -> a.register(Commands.registerCommands(literal(MOD_ID))));

        ServerWorldEvents.LOAD.register((server, level) -> CollisionPairData.load(level));
    }
}
