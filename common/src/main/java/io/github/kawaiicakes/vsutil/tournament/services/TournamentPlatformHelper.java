package io.github.kawaiicakes.vsutil.tournament.services;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.ServiceLoader;

public interface TournamentPlatformHelper {
    static TournamentPlatformHelper get() {
        return ServiceLoader.load(TournamentPlatformHelper.class)
                .findFirst()
                .orElseThrow();
    }

    BakedModel loadBakedModel(ResourceLocation resourceLocation);
}
