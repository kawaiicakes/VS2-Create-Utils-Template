package io.github.kawaiicakes.vsutil.mixin;

import io.github.kawaiicakes.vsutil.VSUtil;
import io.github.kawaiicakes.vsutil.api.DisabledCollisionData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;

@Mixin(ShipObjectServerWorld.class)
public abstract class ShipObjectServerWorldMixin {
    @Shadow(remap = false) public abstract boolean disableCollisionBetweenBodies(long shipId0, long shipId1);

    @Inject(
            method = "createNewShipAtBlock(Lorg/joml/Vector3ic;ZDLjava/lang/String;)Lorg/valkyrienskies/core/impl/game/ships/ShipData;",
            at = @At(value = "RETURN"),
            remap = false
    )
    private void addLogicWhenCreatingShips(
            Vector3ic blockPosInWorldCoordinates,
            boolean createShipObjectImmediately, double scaling, String dimensionId,
            CallbackInfoReturnable<ShipData> cir
    ) {
        try {
            String[] dimStrings = dimensionId.split(":");
            ResourceLocation rl
                    = new ResourceLocation(dimStrings[dimStrings.length - 2], dimStrings[dimStrings.length - 1]);
            ResourceKey<Level> levelKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, rl);

            // This SHOULD be fine to do given that the class injected into should only be calling this in levels that
            // a) exist
            // b) are on the serverside (hence ShipObjectServerWorld)
            ServerLevel level = VSUtil.getCurrentServer().getLevel(levelKey);
            for (long noCollisionShip : DisabledCollisionData.getShips(level)) {
                if (noCollisionShip == cir.getReturnValue().getId()) continue;
                this.disableCollisionBetweenBodies(noCollisionShip, cir.getReturnValue().getId());
            }
        } catch (Exception e) {
            VSUtil.LOGGER.error("Error while disabling collisions for ships!", e);
        }
    }
}
