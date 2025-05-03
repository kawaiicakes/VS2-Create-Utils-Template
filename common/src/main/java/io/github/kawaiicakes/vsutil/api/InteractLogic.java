package io.github.kawaiicakes.vsutil.api;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class InteractLogic {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void interactWith(ServerPlayer player, ServerLevel level, BlockPos blockPos) {
        BlockHitResult hitResult = new BlockHitResult(
                new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.UP, blockPos, true
        );

        final boolean shiftKeyDown = player.getSharedFlag(1);

        // this seems dangerous LOL
        try {
            player.setSharedFlag(1, false);
            player.gameMode.useItemOn(
                    player, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hitResult
            );
        } catch (Exception e) {
            LOGGER.error("Error interacting!", e);
            player.setSharedFlag(1, shiftKeyDown);
        }

        player.setSharedFlag(1, shiftKeyDown);
    }
}
