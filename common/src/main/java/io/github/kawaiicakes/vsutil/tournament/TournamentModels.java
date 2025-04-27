package io.github.kawaiicakes.vsutil.tournament;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kawaiicakes.vsutil.tournament.services.TournamentPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashSet;

public class TournamentModels {
    public static final TournamentModels INSTANCE = new TournamentModels();

    public final LinkedHashSet<ResourceLocation> MODELS = new LinkedHashSet<>();

    private Model model(String name) {
        return this.model(name, true, false);
    }

    @SuppressWarnings("SameParameterValue")
    private Model model(String name, boolean checkSides, boolean useAO) {
        ResourceLocation rl = new ResourceLocation("vs_tournament", name);

        MODELS.add(rl);

        return new Model(rl, checkSides, useAO);
    }

    Model PROP_BIG = model("block/prop_big_prop");
    Model PROP_SMALL = model("block/prop_small_prop");

    private BakedModel getModel(ResourceLocation rl) {
        BakedModel model = TournamentPlatformHelper
                .get()
                .loadBakedModel(rl);

        if (model == null) {
            System.out.println("[Tournament] Failed to load model $rl");
            return Minecraft.getInstance().getModelManager().getMissingModel();
        }

        return model;
    }

    public interface Renderer {
        void render(
                PoseStack matrixStack,
                BlockEntity blockEntity,
                MultiBufferSource bufferSource,
                int packedLight,
                int packedOverlay
        );
    }

    public static class Model {
        public final ResourceLocation resourceLocation;
        public final boolean checkSides;
        public final boolean useAO;

        public Model(
                ResourceLocation resourceLocation,
                boolean checkSides,
                boolean useAO
        ) {
            this.resourceLocation = resourceLocation;
            this.checkSides = checkSides;
            this.useAO = useAO;

            this.renderer = (matrixStack, blockEntity, bufferSource, packedLight, packedOverlay) -> {
                Level level = blockEntity.getLevel();
                if (level == null) return;

                ModelBlockRenderer modRend = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

                if (this.useAO)
                    modRend.tesselateWithAO(
                            level,
                            INSTANCE.getModel(this.resourceLocation),
                            blockEntity.getBlockState(),
                            blockEntity.getBlockPos(),
                            matrixStack,
                            bufferSource.getBuffer(RenderType.cutout()),
                            checkSides,
                            level.random,
                            42L,
                            packedOverlay
                    );
                else
                    modRend.tesselateWithoutAO(
                            level,
                            INSTANCE.getModel(this.resourceLocation),
                            blockEntity.getBlockState(),
                            blockEntity.getBlockPos(),
                            matrixStack,
                            bufferSource.getBuffer(RenderType.cutout()),
                            checkSides,
                            level.random,
                            42L,
                            packedOverlay
                    );
            };
        }

        public final Renderer renderer;
    }
}
