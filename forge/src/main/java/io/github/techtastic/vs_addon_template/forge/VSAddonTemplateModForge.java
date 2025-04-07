package io.github.techtastic.vs_addon_template.forge;

import io.github.techtastic.vs_addon_template.VSAddonTemplateMod;
import io.github.techtastic.vs_addon_template.item.TesterItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.techtastic.vs_addon_template.VSAddonTemplateMod.init;
import static io.github.techtastic.vs_addon_template.VSAddonTemplateMod.initClient;

@Mod(VSAddonTemplateMod.MOD_ID)
public class VSAddonTemplateModForge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VSAddonTemplateMod.MOD_ID);
    public static final RegistryObject<Item> TESTER = ITEMS.register("tester", TesterItem::new);

    public VSAddonTemplateModForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(MOD_BUS);
        MOD_BUS.addListener(this::clientSetup);
        init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        initClient();
    }
}
