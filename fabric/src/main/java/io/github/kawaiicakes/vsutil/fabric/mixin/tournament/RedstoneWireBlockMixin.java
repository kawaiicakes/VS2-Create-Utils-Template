package io.github.kawaiicakes.vsutil.fabric.mixin.tournament;

import io.github.kawaiicakes.vsutil.tournament.block.RedstoneConnectingBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class RedstoneWireBlockMixin {

    @Inject(
            method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void tournament$shouldConnectTo(
            BlockState state,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        if (state != null && state.getBlock() instanceof RedstoneConnectingBlock cb && direction != null) {
            cir.setReturnValue(cb.canConnectTo(state, direction));
        }
    }
}
