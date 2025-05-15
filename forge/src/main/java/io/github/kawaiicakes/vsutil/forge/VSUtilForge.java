package io.github.kawaiicakes.vsutil.forge;

import io.github.kawaiicakes.vsutil.Commands;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.api.DisabledCollisionData;
import io.github.kawaiicakes.vsutil.network.forge.VSUtilPacketsImpl;
import io.github.kawaiicakes.vsutil.tournament.TournamentItems;
import io.github.kawaiicakes.vsutil.tournament.TournamentModels;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static io.github.kawaiicakes.vsutil.VSUtil.*;
import static io.github.kawaiicakes.vsutil.tournament.registry.CreativeTabs.create;
import static net.minecraft.commands.Commands.literal;

@Mod(VSUtil.MOD_ID)
public class VSUtilForge {
    private boolean happenedClientSetup = false;

    public VSUtilForge() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::clientSetup);
        modBus.addListener(this::onModelRegistry);
        modBus.addListener(this::entityRenderers);
        modBus.addListener(this::commonSetup);
        forgeBus.addListener(VSUtilForge::onLevelLoaded);
        forgeBus.addListener(VSUtilForge::onRegisterCommands);

        init();
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(VSUtilPacketsImpl::register);
        event.enqueueWork(() -> Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                TournamentItems.TAB,
                create()
        ));

    }

    @SubscribeEvent
    public void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        initClientRenderers(event::registerBlockEntityRenderer);
    }

    @SubscribeEvent
    public void onModelRegistry(ModelEvent.RegisterAdditional event) {
        TournamentModels.INSTANCE.MODELS.forEach(event::register);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.registerCommands(literal("vsutil")));
    }

    @SubscribeEvent
    public static void onLevelLoaded(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        CollisionPairData.load(serverLevel);
        DisabledCollisionData.load(serverLevel);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event) {
        if (this.happenedClientSetup) {
            return;
        }
        this.happenedClientSetup = true;
        initClient();
    }
}
