package io.github.kawaiicakes.vsutil.mixin;

import com.mojang.authlib.GameProfile;
import io.github.kawaiicakes.vsutil.api.LocalPlayerInterfaceMixin;
import io.github.kawaiicakes.vsutil.screen.PropellerBlockScreen;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player implements LocalPlayerInterfaceMixin {
    private LocalPlayerMixin(
            Level level, BlockPos blockPos, float f,
            GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey
    ) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Override
    public void vsutil$openPropeller(PropellerBlockEntity<?> propeller) {
        if (propeller.isUneditable()) return;

        Minecraft.getInstance().setScreen(
                new PropellerBlockScreen(propeller, propeller.getBlockState().getBlock().getName())
        );
    }
}
