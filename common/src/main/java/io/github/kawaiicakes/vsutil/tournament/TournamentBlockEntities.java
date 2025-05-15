package io.github.kawaiicakes.vsutil.tournament;

import com.mojang.datafixers.types.Type;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import io.github.kawaiicakes.vsutil.tournament.blockentity.render.PropellerBlockEntityRender;
import io.github.kawaiicakes.vsutil.tournament.registry.DeferredRegister;
import io.github.kawaiicakes.vsutil.tournament.registry.RegistrySupplier;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.Util;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static io.github.kawaiicakes.vsutil.VSUtil.MOD_ID;

public class TournamentBlockEntities {
    public static final TournamentBlockEntities INSTANCE = new TournamentBlockEntities();
    private final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            MOD_ID,
            Registries.BLOCK_ENTITY_TYPE
    );

    private final List<RendererEntry<?>> renderers = new ArrayList<>();

    public RegistrySupplier<BlockEntityType<PropellerBlockEntity.BigPropellerBlockEntity>> PROP_BIG;
    public RegistrySupplier<BlockEntityType<PropellerBlockEntity.SmallPropellerBlockEntity>> PROP_SMALL;

    private TournamentBlockEntities() {
        /* ================================================================== */
        PROP_BIG = create(TournamentBlocks.INSTANCE.PROP_BIG)
                .withBE(PropellerBlockEntity.BigPropellerBlockEntity::new)
                .byName("prop_big")
                .withRenderer (
                        () -> new PropellerBlockEntityRender<PropellerBlockEntity.BigPropellerBlockEntity>(
                                TournamentModels.INSTANCE.PROP_BIG
                        )
                )
                .build();
        /* ================================================================== */
        PROP_SMALL = create(TournamentBlocks.INSTANCE.PROP_SMALL)
                .withBE(PropellerBlockEntity.SmallPropellerBlockEntity::new)
                .byName("prop_small")
                .withRenderer (
                    () -> new PropellerBlockEntityRender<PropellerBlockEntity.SmallPropellerBlockEntity>(
                            TournamentModels.INSTANCE.PROP_SMALL
                    )
                )
                .build();
        /* ================================================================== */
    }

    public void register() {
        this.BLOCK_ENTITIES.applyAll();
    }

    private <T extends Block, I extends BlockEntity> EntryBuilder<T, I> create(RegistrySupplier<T> supplier) {
        return new EntryBuilder<>(supplier);
    }

    @SuppressWarnings("unchecked")
    public void initClientRenderers(VSUtil.ClientRenderers clientRenderers) {
        renderers.forEach(
                x -> {
                    BlockEntityRendererProvider<BlockEntity> rp =
                            context -> (BlockEntityRenderer<BlockEntity>) x.renderer.get();

                    clientRenderers.registerBlockEntityRenderer(
                            (BlockEntityType<BlockEntity>) x.type.get(),
                            rp
                    );
                }
        );
    }

    private class EntryBuilder<T, I extends BlockEntity> {
        private final RegistrySupplier<T> blockRegistry;
        private Pair<Set<RegistrySupplier<T>>, BlockEntityType.BlockEntitySupplier<I>> pairedBE;
        private RegistrySupplier<BlockEntityType<I>> beRegistry;

        private EntryBuilder(RegistrySupplier<T> blockRegistry) {
            this.blockRegistry = blockRegistry;
        }

        public EntryBuilder<T, I> withBE(BlockEntityType.BlockEntitySupplier<I> beSupplier) {
            this.pairedBE = Pair.of(Collections.singleton(this.blockRegistry), beSupplier);
            return this;
        }

        @SuppressWarnings("DataFlowIssue")
        private EntryBuilder<T, I> byName(String name) {
            this.beRegistry = TournamentBlockEntities.this.BLOCK_ENTITIES.register(
                    name,
                    () -> {
                        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, name);

                        List<T> regSupplier = this.pairedBE.first().stream().map(RegistrySupplier::get).toList();
                        Block[] blocks = new Block[regSupplier.size()];

                        for (T t : regSupplier) {
                            if (t instanceof Block block)
                                blocks[regSupplier.indexOf(t)] = block;
                        }

                        return BlockEntityType.Builder.of(
                                this.pairedBE.second(),
                                blocks
                        ).build(type);
                    }
            );

            return this;
        }

        public EntryBuilder<T, I> withRenderer(Supplier<Object> renderer) {
            TournamentBlockEntities.this.renderers.add(new RendererEntry<>(this.beRegistry, renderer));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <U extends I, A extends BlockEntityType<U>> RegistrySupplier<A> build() {
            return (RegistrySupplier<A>) this.beRegistry;
        }
    }

    private record RendererEntry<T extends BlockEntity> (
            RegistrySupplier<BlockEntityType<T>> type,
            Supplier<Object> renderer
    ) {}
}
