package io.github.techtastic.vs_addon_template.item;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class TesterItem extends Item {
    public static final Logger LOGGER = LogUtils.getLogger();

    public TesterItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) return super.useOn(context);

        BlockPos shipSpacePosMojangVec = context.getClickedPos();
        LOGGER.info("Clicked pos: {}", shipSpacePosMojangVec);
        Vector3d shipSpacePosJOMLVec = VectorConversionsMCKt.toJOMLD(shipSpacePosMojangVec);
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, shipSpacePosJOMLVec);

        if (ship == null) return super.useOn(context);

        Vector3d realSpacePosJOMLVec = ship.getTransform().getShipToWorld().transformPosition(shipSpacePosJOMLVec);
        BlockPos nearestBlockPos = BlockPos.containing(realSpacePosJOMLVec.x + 0.5, realSpacePosJOMLVec.y + 0.5, realSpacePosJOMLVec.z + 0.5);

        LOGGER.info("Clicked pos (real): {}", nearestBlockPos);

        return super.useOn(context);
    }
}
