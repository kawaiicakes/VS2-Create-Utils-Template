package io.github.kawaiicakes.vsutil.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.api.DisabledCollisionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.shadow.Dy;

@Mixin(Dy.class)
public abstract class ShipObjectServerWorldMixin {
    @Shadow(remap = false) public abstract boolean disableCollisionBetweenBodies(long shipId0, long shipId1);

    @WrapMethod(
            method = "createNewShipAtBlock",
            remap = false
    )
    private ServerShip addLogicWhenCreatingShips(
            Vector3ic shipPosInWorld, boolean createShipImmediately, double scaling, String dimensionId, Operation<ServerShip> original
    ) {
        ServerShip ship = original.call(shipPosInWorld, createShipImmediately, scaling, dimensionId);

        try {
            String[] dimStrings = dimensionId.split(":");
            ResourceLocation rl
                    = new ResourceLocation(dimStrings[dimStrings.length - 2], dimStrings[dimStrings.length - 1]);
            ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, rl);

            // This SHOULD be fine to do given that the class injected into should only be calling this in levels that
            // a) exist
            // b) are on the serverside (hence ShipObjectServerWorld)
            ServerLevel level = VSUtil.getCurrentServer().getLevel(levelKey);
            for (long noCollisionShip : DisabledCollisionData.getShips(level)) {
                if (noCollisionShip == ship.getId()) continue;
                this.disableCollisionBetweenBodies(noCollisionShip, ship.getId());
            }
            
            return ship;
        } catch (Exception e) {
            VSUtil.LOGGER.error("Error while disabling collisions for ships!", e);
            return ship;
        }
    }
}
