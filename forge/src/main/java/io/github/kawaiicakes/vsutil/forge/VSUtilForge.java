package io.github.kawaiicakes.vsutil.forge;

import io.github.kawaiicakes.vsutil.VSUtil;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static io.github.kawaiicakes.vsutil.VSUtil.init;
import static io.github.kawaiicakes.vsutil.VSUtil.initClient;

@Mod(VSUtil.MOD_ID)
public class VSUtilForge {
    public VSUtilForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);
        init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        initClient();
    }
}
