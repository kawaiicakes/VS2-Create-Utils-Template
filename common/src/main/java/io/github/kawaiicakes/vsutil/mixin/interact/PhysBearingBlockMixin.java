package io.github.kawaiicakes.vsutil.mixin.interact;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.kawaiicakes.vsutil.api.InteractLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.clockwork.content.contraptions.phys.bearing.PhysBearingBlock;
import org.valkyrienskies.clockwork.content.contraptions.phys.bearing.PhysBearingBlockEntity;

@Pseudo
@Mixin(PhysBearingBlock.class)
public abstract class PhysBearingBlockMixin {
    @Shadow
    @Final
    private static void use$lambda$0(PhysBearingBlockEntity te) {}

    @WrapMethod(method = "use")
    private InteractionResult use(
            BlockState state, Level worldIn, BlockPos pos,
            Player player, InteractionHand handIn,
            BlockHitResult hit,
            Operation<InteractionResult> original
    ) {
        if (player != null || !InteractLogic.checkArgsMatch(handIn, hit))
            return original.call(state, worldIn, pos, player, handIn, hit);

        /*
            Code past this point is reasonably certain to be executing serverside and called inside InteractLogic
            by a command
         */

        //noinspection deprecation
        if (!worldIn.hasChunkAt(pos))
            return InteractionResult.FAIL;

        if (!(worldIn instanceof ServerLevel level))
            return InteractionResult.FAIL;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PhysBearingBlockEntity pbbe))
            return InteractionResult.FAIL;

        use$lambda$0(pbbe);

        return InteractionResult.SUCCESS;
    }
}
