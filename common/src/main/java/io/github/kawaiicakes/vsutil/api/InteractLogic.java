package io.github.kawaiicakes.vsutil.api;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Arrays;

public class InteractLogic {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation[] SUPPORTED = {
            new ResourceLocation("vs_clockwork", "phys_bearing"),
            new ResourceLocation("vs_clockwork", "propeller_bearing"),
            new ResourceLocation("vs_clockwork", "flap_bearing")
    };

    public static int interactWith(CommandSourceStack source, ServerLevel level, BlockPos blockPos) {
        try {
            //noinspection deprecation
            if (!level.hasChunkAt(blockPos)) {
                source.sendFailure(
                        Component.translatable("error.vsutil.block_not_loaded", blockPos)
                                .withStyle(ChatFormatting.RED)
                );
                return -1;
            }

            BlockState state = level.getBlockState(blockPos);
            ResourceLocation block = Registry.BLOCK.getKey(state.getBlock());

            if (Arrays.stream(SUPPORTED).noneMatch(block::equals)) {
                source.sendFailure(
                        Component.translatable("error.vsutil.block_not_supported", block)
                                .withStyle(ChatFormatting.RED)
                );
                return -1;
            }

            BlockHitResult hitResult = new BlockHitResult(
                    new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.UP, blockPos, true
            );

            //noinspection DataFlowIssue
            state.use(level, null, InteractionHand.OFF_HAND, hitResult);

            return 1;
        } catch (Exception e) {
            LOGGER.error("Error interacting!", e);
            source.sendFailure(
                    Component.translatable("error.vsutil.misc")
                            .withStyle(ChatFormatting.RED)
            );
            throw e;
        }
    }

    public static boolean checkArgsMatch(InteractionHand hand, BlockHitResult hitResult) {
        return InteractionHand.OFF_HAND.equals(hand)
                && hitResult.getDirection().equals(Direction.UP)
                && hitResult.isInside();
    }
}
