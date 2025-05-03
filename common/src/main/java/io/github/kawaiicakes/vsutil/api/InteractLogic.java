package io.github.kawaiicakes.vsutil.api;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class InteractLogic {
    public static void interactWith(ServerPlayer player, ServerLevel level, BlockPos blockPos) {
        BlockHitResult hitResult = new BlockHitResult(
                new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.UP, blockPos, true
        );

        final Pose playerPose = player.getPose();

        // this seems dangerous LOL
        try {
            player.setPose(Pose.STANDING);
            player.gameMode.useItemOn(
                    player, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hitResult
            );
        } catch (Exception e) {
            LogUtils.getLogger().error("Error interacting!", e);
            player.setPose(playerPose);
        }

        player.setPose(playerPose);
    }
}
