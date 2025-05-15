package io.github.kawaiicakes.vsutil.tournament;

import io.github.kawaiicakes.vsutil.item.NoCollisionWand;
import io.github.kawaiicakes.vsutil.tournament.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

public class TournamentItems {
    public static final TournamentItems INSTANCE = new TournamentItems();

    public static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "main_tab")
    );

    public final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public void register() {
        ITEMS.register("collision_wand", NoCollisionWand::new);
        TournamentBlocks.INSTANCE.registerItems(this.ITEMS);
        this.ITEMS.applyAll();
    }

    private TournamentItems() {}
}
