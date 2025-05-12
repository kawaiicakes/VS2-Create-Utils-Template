package io.github.kawaiicakes.vsutil.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Pseudo
@Mixin(AbstractCannonProjectile.class)
public abstract class AbstractCannonProjectileMixin {
    /**
     * Credit to Endal of Tank Tussle
     */
    @WrapOperation(
            method = {"shouldFall"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/phys/AABB;)Z"
            )}
    )
    private boolean accountForShipCollisions(Level level, AABB aabb, Operation<Boolean> original) {
        Stream<Ship> ships = StreamSupport.stream(
                VSGameUtilsKt.getShipsIntersecting(level, aabb).spliterator(), false
        );

        return original.call(level, aabb)
                && ships.allMatch(
                        (ship) -> level.noCollision(VectorConversionsMCKt.toMinecraft(
                                        VectorConversionsMCKt.toJOML(aabb).transform(ship.getWorldToShip())
                        ))
        );
    }
}
