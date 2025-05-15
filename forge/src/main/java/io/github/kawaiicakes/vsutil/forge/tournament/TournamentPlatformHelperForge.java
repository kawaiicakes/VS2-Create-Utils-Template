package io.github.kawaiicakes.vsutil.forge.tournament;

import io.github.kawaiicakes.vsutil.tournament.services.TournamentPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TournamentPlatformHelperForge implements TournamentPlatformHelper {
    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        return Minecraft.getInstance().getModelManager().getModelBakery().getBakedTopLevelModels()
                .getOrDefault(
                        modelLocation,
                        null
                );
    }
}
