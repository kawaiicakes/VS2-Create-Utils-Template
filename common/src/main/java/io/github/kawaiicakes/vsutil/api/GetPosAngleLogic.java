package io.github.kawaiicakes.vsutil.api;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class GetPosAngleLogic {
    public static Vec3 getEuler(Ship ship) {
        return VectorConversionsMCKt.toMinecraft(
                ship.getTransform()
                        .getShipToWorldRotation()
                        .getEulerAnglesXYZ(new Vector3d())
        );
    }

    public static Vec3 getPos(Ship ship) {
        return VectorConversionsMCKt.toMinecraft(ship.getTransform().getPositionInWorld());
    }

    public static Vec3 getMotion(Ship ship) {
        return VectorConversionsMCKt.toMinecraft(ship.getVelocity());
    }

    public static Vec3 getOmega(Ship ship) {
        return VectorConversionsMCKt.toMinecraft(ship.getOmega());
    }
}
