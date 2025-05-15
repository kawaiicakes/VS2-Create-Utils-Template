package io.github.kawaiicakes.vsutil.tournament.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kawaiicakes.vsutil.tournament.TournamentModels;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.DirectionalBlock;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PropellerBlockEntityRender<T extends PropellerBlockEntity<T>> implements BlockEntityRenderer<T> {
    private final TournamentModels.Model model;

    public PropellerBlockEntityRender(TournamentModels.Model model) {
        this.model = model;
    }

    @Override
    public void render(
            T blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(blockEntity.getBlockState().getValue(DirectionalBlock.FACING).getOpposite().getRotation());
        poseStack.mulPose(
                new Quaternionf(new AxisAngle4f((float) Math.toRadians(blockEntity.rotation), 0f, 1f, 0f))
        );
        poseStack.translate(-0.5, -0.5, -0.5);

        this.model.renderer.render(
                poseStack,
                blockEntity,
                bufferSource,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }
}
