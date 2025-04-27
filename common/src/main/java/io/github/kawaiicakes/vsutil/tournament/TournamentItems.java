package io.github.kawaiicakes.vsutil.tournament;

import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.tournament.registry.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class TournamentItems {
    public static final TournamentItems INSTANCE = new TournamentItems();

    public CreativeModeTab TAB;

    private final DeferredRegister<Item> ITEMS = DeferredRegister.create(VSUtil.MOD_ID, Registry.ITEM_REGISTRY);

    public void register() {
        TournamentBlocks.INSTANCE.registerItems(this.ITEMS);
        this.ITEMS.applyAll();
    }

    private TournamentItems() {}
}
