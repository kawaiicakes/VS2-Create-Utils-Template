package io.github.kawaiicakes.vsutil.fabric.tournament;

import io.github.kawaiicakes.vsutil.tournament.services.TournamentPlatformHelper;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TournamentPlatformHelperFabric implements TournamentPlatformHelper {
    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        //noinspection deprecation
        return BakedModelManagerHelper.getModel(
                Minecraft.getInstance().getModelManager(),
                modelLocation
        );
    }
}
