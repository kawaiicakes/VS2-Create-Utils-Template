package io.github.kawaiicakes.vsutil.tournament.services;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface TournamentPlatformHelper {
    static TournamentPlatformHelper get() {
        return ServiceLoader.load(TournamentPlatformHelper.class)
                .findFirst()
                .orElseThrow();
    }

    CreativeModeTab createCreativeTab(ResourceLocation id, Supplier<ItemStack> stack);

    BakedModel loadBakedModel(ResourceLocation resourceLocation);
}
