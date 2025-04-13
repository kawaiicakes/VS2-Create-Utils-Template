package io.github.kawaiicakes.vsutil.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

public class ShipifyLogic {
    public static Double MIN_SCALING = null;

    public static double getMinScaling() {
        if (MIN_SCALING == null) MIN_SCALING = VSGameConfig.SERVER.getMinScaling();
        return MIN_SCALING;
    }

    public static void shipify(
            ServerLevel level, BlockPos pos,
            double scale, String slug, boolean setStatic, boolean noCollision,
            @Nullable ServerPlayer player
    ) {
        var parentShip = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (!level.getBlockState(pos).isAir()) {
            // Make a ship
            String dimensionId = VSGameUtilsKt.getDimensionId(level);

            ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(
                    VectorConversionsMCKt.toJOML(pos), false, scale, dimensionId
            );

            BlockPos centerPos = VectorConversionsMCKt.toBlockPos(
                    serverShip.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i())
            );

            // Move the block from the world to a ship
            RelocationUtilKt.relocateBlock(level, pos, centerPos, true, serverShip, Rotation.NONE);

            if (player != null)
                player.sendSystemMessage(Component.literal("SHIPIFIED!"));

            if (!slug.isEmpty())
                serverShip.setSlug(slug);

            if (noCollision) {
                for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, serverShip.getWorldAABB())) {
                    VSGameUtilsKt.getShipObjectWorld(level).disableCollisionBetweenBodies(serverShip.getId(), ship.getId());
                    CollisionPairData.add(serverShip.getId(), ship.getId());
                }
            }

            serverShip.setStatic(setStatic);

            if (parentShip == null) return;

            // Compute the ship transform
            Vector3d newShipPosInWorld = parentShip.getShipToWorld()
                    .transformPosition(VectorConversionsMCKt.toJOMLD(pos).add(0.5, 0.5, 0.5));
            Vector3d newShipPosInShipyard = VectorConversionsMCKt.toJOMLD(pos).add(0.5, 0.5, 0.5);
            Quaterniondc newShipRotation = parentShip.getTransform().getShipToWorldRotation();
            var newShipScaling = parentShip.getTransform().getShipToWorldScaling().mul(scale, new Vector3d());
            if (newShipScaling.x() < getMinScaling()) {
                // Do not allow scaling to go below minScaling
                newShipScaling = new Vector3d(getMinScaling(), getMinScaling(), getMinScaling());
            }

            ShipTransformImpl shipTransform =
                    new ShipTransformImpl(newShipPosInWorld, newShipPosInShipyard, newShipRotation, newShipScaling);
            ((ShipDataCommon) serverShip).setTransform(shipTransform);
        }
    }
}
