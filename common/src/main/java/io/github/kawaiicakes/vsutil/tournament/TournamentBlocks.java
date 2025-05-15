package io.github.kawaiicakes.vsutil.tournament;

import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.tournament.block.PropellerBlock;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import io.github.kawaiicakes.vsutil.tournament.registry.DeferredRegister;
import io.github.kawaiicakes.vsutil.tournament.registry.RegistrySupplier;
import it.unimi.dsi.fastutil.Pair;
import kotlin.jvm.functions.Function0;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class TournamentBlocks {
    public static final TournamentBlocks INSTANCE = new TournamentBlocks();

    private final DeferredRegister<Block> BLOCKS = DeferredRegister.create(VSUtil.MOD_ID, Registries.BLOCK);
    public final List<Pair<String, Function0<? extends BlockItem>>> ITEMS = new ArrayList<>();

    public RegistrySupplier<PropellerBlock> PROP_BIG;
    public RegistrySupplier<PropellerBlock> PROP_SMALL;

    private TournamentBlocks() {}

    public void register() {
        PROP_BIG = register(
                "prop_big",
                () -> new PropellerBlock(PropellerBlockEntity.BigPropellerBlockEntity::new)
        );

        PROP_SMALL = register(
                "prop_small",
                () -> new PropellerBlock(PropellerBlockEntity.SmallPropellerBlockEntity::new)
        );

        this.BLOCKS.applyAll();
    }

    private <T extends Block> RegistrySupplier<T> register(String name, Function0<T> block) {
        RegistrySupplier<T> supplier = this.BLOCKS.register(name, block);
        this.ITEMS.add(Pair.of(
                name, () -> new BlockItem(supplier.get(), new Item.Properties()))
        );
        return supplier;
    }

    public void registerItems(DeferredRegister<Item> items) {
        this.ITEMS.forEach(pair -> items.register(pair.first(), pair.second()));
    }
}
