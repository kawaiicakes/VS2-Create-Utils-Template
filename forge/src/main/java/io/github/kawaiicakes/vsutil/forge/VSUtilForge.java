package io.github.kawaiicakes.vsutil.forge;

import io.github.kawaiicakes.vsutil.api.CollisionPairData;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.item.NoCollisionWand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.kawaiicakes.vsutil.VSUtil.*;

@Mod(VSUtil.MOD_ID)
public class VSUtilForge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> COLLISION_WAND
            = ITEMS.register("collision_wand", NoCollisionWand::new);

    public VSUtilForge() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::clientSetup);
        forgeBus.addListener(VSUtilForge::onLevelLoaded);

        ITEMS.register(modBus);

        init();
    }

    @SubscribeEvent
    public static void onLevelLoaded(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        CollisionPairData.load(serverLevel);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        initClient();
    }
}
