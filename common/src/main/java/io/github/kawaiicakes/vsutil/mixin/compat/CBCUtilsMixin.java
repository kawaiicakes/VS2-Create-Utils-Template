package io.github.kawaiicakes.vsutil.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import rbasamoyai.createbigcannons.utils.CBCUtils;

@Pseudo
@Mixin(CBCUtils.class)
public abstract class CBCUtilsMixin {
    /**
     * Credit to Endal of Tank Tussle
     */
    @WrapMethod(method = "getSurfaceNormalVector(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    private static Vec3 recalculateNormalOnShips(Level level, BlockPos hitPos, Vec3 normal, Operation<Vec3> original) {
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, hitPos);

        if (ship == null) return original.call(level, hitPos, normal);

        return VectorConversionsMCKt.toMinecraft(
                ship.getShipToWorld().transformDirection(VectorConversionsMCKt.toJOML(normal))
        );
    }

    @WrapMethod(method = "playBlastLikeSoundOnServer")
    private static void translateShipSoundToWorld(
            ServerLevel level,
            double x, double y, double z,
            SoundEvent soundEvent, SoundSource soundSource,
            float volume, float pitch, float airAbsorption,
            Operation<Void> original
    ) {
        Vector3d jomlVec = new Vector3d(x, y, z);
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, jomlVec);
        if (ship == null) {
            original.call(level, x, y, z, soundEvent, soundSource, volume, pitch, airAbsorption);
            return;
        }
        Vector3d transformedPosition = ship.getTransform().getShipToWorld().transformPosition(jomlVec);

        original.call(
                level,
                transformedPosition.x, transformedPosition.y, transformedPosition.z,
                soundEvent, soundSource,
                volume, pitch, airAbsorption
        );
    }
}
