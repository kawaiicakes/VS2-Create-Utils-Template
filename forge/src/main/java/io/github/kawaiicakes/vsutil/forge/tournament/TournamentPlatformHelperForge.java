package io.github.kawaiicakes.vsutil.forge.tournament;

import io.github.kawaiicakes.vsutil.tournament.services.TournamentPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TournamentPlatformHelperForge implements TournamentPlatformHelper {
    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Supplier<ItemStack> stack) {
        return new CreativeModeTab(id.toString().replace(":", ".")) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return stack.get();
            }

            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable(
                        "itemGroup." + String.format("%s.%s", id.getNamespace(), id.getPath())
                );
            }
        };
    }

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
