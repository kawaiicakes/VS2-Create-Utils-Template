package io.github.kawaiicakes.vsutil.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public abstract class UpdatePropellerPacket {
    @ExpectPlatform
    public static UpdatePropellerPacket create(
            ResourceKey<Level> level, BlockPos pos,
            double force, float maxSpeed, float accel,
            boolean finalize
    ) {
        throw new AssertionError();
    }
}
