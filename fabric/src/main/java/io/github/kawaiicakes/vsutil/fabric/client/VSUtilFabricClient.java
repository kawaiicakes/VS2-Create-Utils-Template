package io.github.kawaiicakes.vsutil.fabric.client;

import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.tournament.TournamentModels;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class VSUtilFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VSUtil.initClient();
        VSUtil.initClientRenderers(new ClientRenderersFabric());

        //noinspection deprecation
        ModelLoadingRegistry.INSTANCE.registerModelProvider(
                (manager, out) -> TournamentModels.INSTANCE.MODELS.forEach(out)
        );
    }

    private static class ClientRenderersFabric implements VSUtil.ClientRenderers {
        @Override
        public <T extends BlockEntity> void registerBlockEntityRenderer(
                @NotNull BlockEntityType<T> t,
                @NotNull BlockEntityRendererProvider<T> r) {
            //noinspection deprecation
            BlockEntityRendererRegistry.register(t, r);
        }
    }
}
